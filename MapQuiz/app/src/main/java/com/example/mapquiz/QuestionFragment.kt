package com.example.mapquiz

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore
import com.mousebird.maply.*
import okio.Okio
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

class QuestionFragment : GlobeMapFragment() {
    private val vectors = ArrayList<VectorObject>()
    private var selectedComponentObject: ComponentObject? = null
    private var selectedComponentObjectOld : ComponentObject? = null
    private var selectedComponentObject2 : ComponentObject? = null
    private var firestore: FirebaseFirestore? = null

    interface OnUserDidSelectListener{
        fun onUserDidSelect(country: String, vectorObject: VectorObject)
    }

    private var mOnUserDidSelectListener : OnUserDidSelectListener? = null

    override fun chooseDisplayType(): MapDisplayType {
        println("chooseDisplayType QuestionFragment")
        return MapDisplayType.Globe
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            inState: Bundle?
    ): View? {

        println("onCreateView QuestionFragment")


        super.onCreateView(inflater, container, inState)

        this.firestore = FirebaseFirestore.getInstance()

        // Do app specific setup logic.
        return baseControl.contentView
    }


    public override fun controlHasStarted() {
        val mbTileFile: File

        println("controlHasStarted QuestionFragment")


        try {
            mbTileFile = "geography-class_medres.mbtiles".getMBTileAsset()
        } catch (e: IOException) {
            Log.d("HelloEarth", e.localizedMessage)
            return
        }

        val mbTileFetcher = MBTileFetcher(mbTileFile)

        val params = SamplingParams()
        params.coordSystem = SphericalMercatorCoordSystem()
        params.coverPoles = true
        params.edgeMatching = true
        params.singleLevel = true
        params.minZoom = 0
        params.maxZoom = mbTileFetcher.maxZoom

        val loader = QuadImageLoader(params, mbTileFetcher.tileInfo, baseControl)
        loader.tileFetcher = mbTileFetcher

        val latitude = 40.5023056 * Math.PI / 180
        val longitude = -3.6704803 * Math.PI / 180
        val zoom_earth_radius = 0.5

        globeControl.animatePositionGeo(longitude, latitude, zoom_earth_radius, 1.0)

        // Tell the controller that this object is the gesture delegate.
        globeControl.gestureDelegate = this

        try {
            overlayCountries(globeControl)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun overlayCountries(baseVC: BaseController) {
        val vectorInfo = VectorInfo()
        vectorInfo.setColor(Color.RED)
        vectorInfo.setLineWidth(5.0f)

        val assetMgr = activity!!.assets
        val paths = assetMgr.list("country_json_50m")

        if (paths != null) {
            for (path in paths) {
                val stream = assetMgr.open("country_json_50m/" + path)
                try {
                    val vecObject = VectorObject()
                    vecObject.selectable = true

                    val json = Okio.buffer(Okio.source(stream)).readUtf8()
                    if (vecObject.fromGeoJSON(json)) {
                        vectors.add(vecObject)
                    }
                } finally {
                    try {
                        stream.close()
                    } catch (e : IOException) {
                    }
                }
            }
        }

        // Add as red
        val compObj =
                baseVC.addVectors(vectors, vectorInfo, RenderControllerInterface.ThreadMode.ThreadAny)
        // Then change to white
        val newVectorInfo = VectorInfo()
        newVectorInfo.setColor(Color.WHITE)
        newVectorInfo.setLineWidth(5.0f)
        baseVC.changeVector(compObj, newVectorInfo, RenderControllerInterface.ThreadMode.ThreadAny)
    }

    private fun String.getMBTileAsset(): File {
        val wrapper = ContextWrapper(activity)
        val mbTilesDirectory = wrapper.getDir("mbtiles", Context.MODE_PRIVATE)
        val inputStream = activity!!.assets.open("mbtiles/${this}")
        val of = File(mbTilesDirectory, this)

        if (!of.exists()) {
            val os: OutputStream = FileOutputStream(of)
            val mBuffer = ByteArray(4096)
            var length = inputStream.read(mBuffer)
            while (length > 0) {
                os.write(mBuffer, 0, length)
                length = inputStream.read(mBuffer)
            }
            os.flush()
            os.close()
            inputStream.close()
        }
        return of
    }

    override fun userDidSelect(globeControl: GlobeController?, selObjs: Array<out SelectedObject>?, loc: Point2d?, screenLoc: Point2d?) {
        super.userDidSelect(globeControl, selObjs, loc, screenLoc)

        if (selObjs?.get(0)?.selObj is VectorObject) {
            val vectorObject = selObjs[0].selObj as VectorObject
            val attributes = vectorObject.attributes

            mOnUserDidSelectListener?.onUserDidSelect(attributes.getString("ADMIN"), vectorObject)
        }
    }


    override fun onAttach(context: Context) {
        println("onAttach QuestionFragment")

        super.onAttach(context)

        mOnUserDidSelectListener = context as OnUserDidSelectListener
    }
    
    fun drawCountry(color: Int, country: String, vectorObject: VectorObject){
        println("draw")
        selectedComponentObjectOld = selectedComponentObject

        // Re-add the object with different info
        val vectorInfo = VectorInfo()
        vectorInfo.setColor(color)
        vectorInfo.setLineWidth(5.0f)
        vectorInfo.drawPriority = Int.MAX_VALUE // Make sure it draws on top of unselected vector

        if(color == Color.BLACK){

            selectedComponentObject = globeControl?.addVector(vectorObject, vectorInfo, RenderControllerInterface.ThreadMode.ThreadAny)
        }
        else if(color == Color.RED){
            globeControl.changeVector(selectedComponentObject, vectorInfo, RenderControllerInterface.ThreadMode.ThreadAny)
            Thread.sleep(2_000)
            drawRightCountry(country)
        }else{
            globeControl.changeVector(selectedComponentObject, vectorInfo, RenderControllerInterface.ThreadMode.ThreadAny)
            Thread.sleep(2_000)
        }

    }

    fun drawRightCountry(country: String){
        for(vectorO in vectors){
            val attributes = vectorO.attributes
            val adminName = attributes.getString("ADMIN")

            if(Translate().translate(adminName) == country){
                Translate().translate(adminName)?.let { replaceCountry(vectorO, it) }

                // Re-add the object with different info
                val vectorInfo = VectorInfo()
                vectorInfo.setColor(Color.GREEN)
                vectorInfo.setLineWidth(5.0f)
                vectorInfo.drawPriority = Int.MAX_VALUE // Make sure it draws on top of unselected vector

                selectedComponentObject2 = globeControl?.addVector(vectorO, vectorInfo, RenderControllerInterface.ThreadMode.ThreadAny)

                break
            }
        }
        Thread.sleep(2_000)
    }

    fun drawCountry(){
        // Re-add the object with different info
        val vectorInfo = VectorInfo()
        vectorInfo.setColor(Color.WHITE)
        vectorInfo.setLineWidth(5.0f)
        vectorInfo.drawPriority = Int.MAX_VALUE // Make sure it draws on top of unselected vector

        globeControl.changeVector(selectedComponentObjectOld, vectorInfo, RenderControllerInterface.ThreadMode.ThreadAny)
        globeControl.changeVector(selectedComponentObject2, vectorInfo, RenderControllerInterface.ThreadMode.ThreadAny)
    }

    private fun replaceCountry(vectorObject: VectorObject, countryTR: String){
        val latitude : Double
        val longitude: Double
        val zoom_earth_radius = 0.8

        if(countryTR == "Rusya"){
            latitude = 1.0
            longitude = 1.0
        }
        else if(countryTR == "Fransa"){
            latitude = 0.08
            longitude = 0.81
        }
        else if(countryTR == "Hollanda"){
            latitude = 0.09
            longitude = 0.91
        }
        else if(countryTR == "İspanya"){
            latitude = -0.072
            longitude = 0.702
        }
        else if(countryTR == "Portekiz"){
            latitude = -0.146
            longitude = 0.715
        }
        else if(countryTR == "Yeni Zelanda"){
            latitude = 2.96
            longitude = -0.76
        }
        else if(countryTR == "ABD"){
            latitude = -1.85
            longitude = 0.67
        }
        else if(countryTR == "Şili"){
            latitude = -1.24
            longitude = -0.58
        }
        else{
            latitude =   vectorObject.center().x
            longitude =   vectorObject.center().y
        }

        globeControl.animatePositionGeo(latitude, longitude, zoom_earth_radius, 1.0)
    }
}



