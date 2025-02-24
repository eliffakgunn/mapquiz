package com.example.mapquiz

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mousebird.maply.*
import okio.Okio
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.ArrayList

open class GlobeFragment : GlobeMapFragment(){
    private val vectors = ArrayList<VectorObject>()
    //private var selectedComponentObject: ComponentObject? = null

    override fun chooseDisplayType(): MapDisplayType {
        println("chooseDisplayType LocalGlobeFragment")
        return MapDisplayType.Globe
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            inState: Bundle?
    ): View? {

        println("onCreateViewonCreateView LocalGlobeFragment")


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

        var latitude = 390.0
        var longitude = 350.0
        var zoom_earth_radius = 0.5

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
        var paths = assetMgr.list("country_json_50m")

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

}