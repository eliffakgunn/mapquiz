package com.example.flutter_mapquiz

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
//import com.bumptech.glide.Glide
/*import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference*/

import com.mousebird.maply.*
import com.squareup.picasso.Picasso
//import com.squareup.picasso.Picasso
import okio.Okio
import org.json.JSONArray
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*
import android.app.Fragment

class LocalGlobeFragment : GlobeMapFragment() {
    private val vectors = ArrayList<VectorObject>()
    private var selectedComponentObject: ComponentObject? = null

    override fun chooseDisplayType(): MapDisplayType {
        return MapDisplayType.Globe
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            inState: Bundle?
    ): View? {

        super.onCreateView(inflater, container, inState)

        // Do app specific setup logic.
        return baseControl.contentView
    }


    public override fun controlHasStarted() {
        //super.controlHasStarted()

        val mbTileFile: File

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

        val latitude =  50 * Math.PI / 180 //40+10
        val longitude =  36 * Math.PI / 180 //14

        val zoom_earth_radius = 1.0

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

        val assetMgr = requireActivity().assets
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
        val inputStream = requireActivity().assets.open("mbtiles/${this}")
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

    //@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun userDidSelect(globeControl: GlobeController?, selObjs: Array<out SelectedObject>?, loc: Point2d?, screenLoc: Point2d?) {
        super.userDidSelect(globeControl, selObjs, loc, screenLoc)

        if( vectorObj != null && globeControl != null){
            val vectorInfo = VectorInfo()
            vectorInfo.setColor(Color.argb(255,255,255,255)) // white
            vectorInfo.setLineWidth(5.0f)
            vectorInfo.drawPriority = Int.MAX_VALUE // Make sure it draws on top of unselected vector

            globeControl.changeVector(selectedComponentObject, vectorInfo, RenderControllerInterface.ThreadMode.ThreadAny)
        }

        if (selObjs?.get(0)?.selObj is VectorObject) {
            val vectorObject = selObjs[0].selObj as VectorObject

            val attributes = vectorObject.attributes
            val adminName = attributes.getString("ADMIN")

            addSelectedObject(vectorObject)

            Translate().translate(adminName)?.let { replaceCountry(vectorObject, it) }
            Translate().translate(adminName)?.let { popup(it) }
        }
    }

    private fun replaceCountry(vectorObject: VectorObject, countryTR: String){
        val latitude : Double
        val longitude: Double
        val zoom_earth_radius = 1.0

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


    //@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("InflateParams")
    private fun popup(name: String){
        val customView = LayoutInflater.from(context).inflate(R.layout.dialog_show_country, null)
        val db = FirebaseFirestore.getInstance()

        WindowManager.LayoutParams.WRAP_CONTENT + 10
        val popupWindow = PopupWindow(
                customView,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT
        )

        popupWindow.elevation = 5.0f

        popupWindow.setBackgroundDrawable(BitmapDrawable())
        popupWindow.isOutsideTouchable = true
        popupWindow.isOutsideTouchable = true

        val txt_country = customView?.findViewById<TextView>(R.id.text_country)
        val img_country = customView?.findViewById<ImageView>(R.id.img_county)
        val txt_country_info = customView?.findViewById<TextView>(R.id.text_country_info)

        txt_country!!.text = name
        txt_country_info?.text = "Bilgi bulunamadı"


        db.collection("Bilgiler")
                .document(name)
                .collection(name)
                .get()
                .addOnSuccessListener { documents ->
                    if(!documents.isEmpty) {
                        val info = documents.documents[0].get("bilgi")
                        val url = documents.documents[0].get("url")
                        FirebaseStorage.getInstance().getReferenceFromUrl(url as String)

                        txt_country_info?.text = info as CharSequence?
                        Picasso.get().load(url).into(img_country)
                    }
                }
                .addOnFailureListener {
                    println("SORGU HATASI")
                }

        customView.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )

        val mWidth= this.resources.displayMetrics.widthPixels;
        val mHeight= this.resources.displayMetrics.heightPixels;

        val x = (mWidth/2).toDouble()
        val y = (mHeight/2).toDouble()

        //popupWindow.showAtLocation(activity?.window?.decorView, Gravity.NO_GRAVITY, x.toInt() - 90 , y.toInt()- customView.measuredHeight +100 )
        popupWindow.showAtLocation(activity?.window?.decorView, Gravity.NO_GRAVITY, x.toInt() -customView.measuredWidth/2 + 50 , y.toInt()- customView.measuredHeight +75)
        //popupWindow.showAtLocation(activity?.window?.decorView, Gravity.NO_GRAVITY, x.toInt() -customView.measuredWidth/2 + 50 , (y.toInt()- customView.measuredHeight+75))
        //popupWindow.showAtLocation(activity?.window?.decorView, Gravity.NO_GRAVITY, x.toInt() -customView.measuredWidth/2 + 50 , (y.toInt()- 5*customView.measuredHeight/4))
        //popupWindow.showAtLocation(activity?.window?.decorView, Gravity.NO_GRAVITY, x.toInt()  , y.toInt())
    }

    private fun addSelectedObject(vectorObject: VectorObject) {
        vectorObj = vectorObject

        // Re-add the object with different info
        val vectorInfo = VectorInfo()
        vectorInfo.setColor(Color.argb(255,255,0,0)) // Gold
        vectorInfo.setLineWidth(5.0f)
        vectorInfo.drawPriority = Int.MAX_VALUE // Make sure it draws on top of unselected vector

        selectedComponentObject = globeControl.addVector(vectorObject, vectorInfo, RenderControllerInterface.ThreadMode.ThreadAny)
    }

    private var vectorObj : VectorObject? = null
}



