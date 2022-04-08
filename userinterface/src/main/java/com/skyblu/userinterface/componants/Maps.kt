package com.skyblu.userinterface.componants

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import com.skyblu.configuration.*
import com.skyblu.models.jump.*
import com.skyblu.userinterface.BuildConfig.GOOGLE_MAPS_API_KEY
import com.skyblu.userinterface.R
import timber.log.Timber

@Composable
fun JumpMap(
    isLoading : Boolean,
    points : List<SkydiveDataPoint>
){



    Timber.d("LENGTH ${points.size}")

    val cameraPadding = 150
    val cameraPositionState : CameraPositionState = rememberCameraPositionState()


    GoogleMap(
        modifier = Modifier.fillMaxSize(),

        properties = MapProperties(
            isMyLocationEnabled = false,
            latLngBoundsForCameraTarget = points.bounds(),
            mapType = MapType.SATELLITE

        ),
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
            compassEnabled = true,
            rotationGesturesEnabled = true,
            scrollGesturesEnabled = true,
            zoomGesturesEnabled = true,
            tiltGesturesEnabled = true,
            zoomControlsEnabled = true,
            mapToolbarEnabled = false
        )
    ) {


        if(!isLoading){
            //Move Camera to Fit Bounds of Jump


                val al = points.filterByPhase(SkydivePhase.CANOPY).toMutableList()
            val ff =  points.filterByPhase(SkydivePhase.FREEFALL).toMutableList()
            val p = al + ff

            val walkingLine: MutableList<LatLng> = points.filterByPhase(SkydivePhase.WALKING).latLngList().toMutableList()
            val aircraftLine: MutableList<LatLng> = points.filterByPhase(SkydivePhase.AIRCRAFT).latLngList().toMutableList()

            val freefallLine: MutableList<LatLng> = points.filterByPhase(SkydivePhase.FREEFALL).latLngList().toMutableList()
            val canopyLine: MutableList<LatLng> = points.filterByPhase(SkydivePhase.CANOPY).latLngList().toMutableList()
            val landedLine: MutableList<LatLng> = points.filterByPhase(SkydivePhase.FREEFALL).latLngList().toMutableList()



            if(landedLine.isNotEmpty() && aircraftLine.isNotEmpty() && freefallLine.isNotEmpty()){
                aircraftLine.firstOrNull()?.let { walkingLine.add(it) }
                freefallLine.firstOrNull()?.let { aircraftLine.add(it) }
                canopyLine.firstOrNull()?.let { freefallLine.add(it) }
//            landedLine.firstOrNull()?.let { canopyLine.add(it) }
            }



            p.bounds()?.let { CameraUpdateFactory.newLatLngBounds(it,  cameraPadding) }
                ?.let { cameraPositionState.move(it) }


            CreatePolyLine(
                settings = PolylineSettings.LandedPolyLineSettings,
                points = landedLine
            )
            CreatePolyLine(
                settings = PolylineSettings.WalkingPolyLineSettings,
                points = walkingLine
            )
            CreatePolyLine(
                settings = PolylineSettings.FreefallPolyLineSettings,
                points = freefallLine
            )
            CreatePolyLine(
                settings = PolylineSettings.AircraftPolyLineSettings,
                points = aircraftLine
            )
            CreatePolyLine(
                settings = PolylineSettings.CanopyPolyLineSettings,
                points = canopyLine
            )

        }


    }
}

@Composable
fun LiveJumpMap(
    cameraPositionState: CameraPositionState,
    points : List<SkydiveDataPoint>,
){
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
        )
    ){

        CreatePolyLine(
            settings = PolylineSettings.CanopyPolyLineSettings,
            points = points.filterByPhase(SkydivePhase.CANOPY).latLngList()
        )
        CreatePolyLine(
            settings = PolylineSettings.WalkingPolyLineSettings,
            points = points.filterByPhase(SkydivePhase.WALKING).latLngList()
        )
        CreatePolyLine(
            settings = PolylineSettings.AircraftPolyLineSettings,
            points = points.filterByPhase(SkydivePhase.AIRCRAFT).latLngList()
        )
        CreatePolyLine(
            settings = PolylineSettings.FreefallPolyLineSettings,
            points = points.filterByPhase(SkydivePhase.FREEFALL).latLngList()
        )
        CreatePolyLine(
            settings = PolylineSettings.LandedPolyLineSettings,
            points = points.filterByPhase(SkydivePhase.LANDED).latLngList()
        )

    }
}

@Preview
@Composable
fun TrackMap(
    skydive : Jump = generateSampleJump(),
    trackingData: JumpTrackingData = generateSampleTrackingData()
){
    val trackingData = trackingData
    val cameraPadding = 5
    val cameraPositionState : CameraPositionState = rememberCameraPositionState()

    GoogleMap(
        modifier = Modifier.fillMaxSize(),

        properties = MapProperties(
            isMyLocationEnabled = false,
            latLngBoundsForCameraTarget = trackingData.getCameraBounds(),
            minZoomPreference = 10f,


        ),
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
            compassEnabled = true,
            rotationGesturesEnabled = true,
            scrollGesturesEnabled = true,
            zoomGesturesEnabled = true,
            tiltGesturesEnabled = true,
            zoomControlsEnabled = true,
            mapToolbarEnabled = false
        )
    ) {


        //Move Camera to Fit Bounds of Jump
        cameraPositionState.move(CameraUpdateFactory.newLatLngBounds(trackingData.getCameraBounds(), 350, 350, cameraPadding))

        val walkingLine: List<LatLng> = trackingData.createLatLngList(trackingData.walkingTrackingPoints)
        val aircraftLine: List<LatLng> = trackingData.createLatLngList(trackingData.aircraftTrackingPoints)
        val freefallLine: List<LatLng> = trackingData.createLatLngList(trackingData.freefallTrackingPoints)
        val canopyLine: List<LatLng> = trackingData.createLatLngList(trackingData.canopyTrackingPoints)
        val landedLine: List<LatLng> = trackingData.createLatLngList(trackingData.landedTrackingPoints)

        CreatePolyLine(
            settings = PolylineSettings.WalkingPolyLineSettings,
            points = walkingLine
        )
        CreatePolyLine(
            settings = PolylineSettings.FreefallPolyLineSettings,
            points = freefallLine
        )
        CreatePolyLine(
            settings = PolylineSettings.AircraftPolyLineSettings,
            points = aircraftLine
        )
        CreatePolyLine(
            settings = PolylineSettings.CanopyPolyLineSettings,
            points = canopyLine
        )
        Marker(
            position = canopyLine.last(),
            icon = bitmapDescriptorFromVector(context = LocalContext.current, icon = R.drawable.parachute_cap),
        )
        CreatePolyLine(
            settings = PolylineSettings.LandedPolyLineSettings,
            points = landedLine
        )

    }
}

sealed class PolylineSettings(
    val color: Color,
    val hex : String = "0xFF0000",
    val width : Float = 8f,
    val pattern : List<PatternItem>? = null,
    @DrawableRes val cap : Int? = null
) {
    object WalkingPolyLineSettings : PolylineSettings(WALKING_COLOR, hex = "0x808080FF" )
    object AircraftPolyLineSettings : PolylineSettings(AIRCRAFT_COLOR, cap = R.drawable.blue_plane, hex = "0xFF53E2F1")
    object FreefallPolyLineSettings : PolylineSettings(FREEFALL_COLOR, hex = "0xFF2644FF")
    object CanopyPolyLineSettings : PolylineSettings(CANOPY_COLOR, hex = "0xFFE64A19",  cap = ((R.drawable.parachute_cap)))
    object LandedPolyLineSettings : PolylineSettings(LANDED_COLOR, hex =  "0xFF000000")
}

fun pathToString(trackingPoints : List<SkydiveDataPoint>, settings : PolylineSettings) : String {
    val stringPrepend = "&path=color:${settings.hex}|weight:5"
    var stringAppend = ""
    for (trackingPoint in trackingPoints) {
        val lat = trackingPoint.latitude
        val long = trackingPoint.longitude
        stringAppend += "|${lat},${long}"
    }
    return  stringPrepend + stringAppend
}


@Composable
private fun CreatePolyLine(settings : PolylineSettings, points : List<LatLng>) : Unit{
    var pCap : Cap = ButtCap()
    if(settings.cap != null){

        pCap = CustomCap(bitmapDescriptorFromVector(context = LocalContext.current, icon = settings.cap)!!,
            10.0F
        )
    }

    return Polyline(
        points = points,
        color = settings.color,
        width = settings.width,
        pattern = settings.pattern,
        endCap = pCap,
        jointType = JointType.ROUND

    )
}


@Composable
private fun bitmapDescriptorFromVector(context: Context, icon: Int): BitmapDescriptor? {
    return ContextCompat.getDrawable(context, icon)?.run {
        setBounds(0, 0, intrinsicWidth, intrinsicHeight)
        val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
        draw(android.graphics.Canvas(bitmap))
        BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}





fun LatLng.stringConvert() : String{
    return "$latitude,$longitude"
}

@Preview(showBackground = true)
@Composable
fun StaticGoogleMap(
    skydive: Jump = generateSampleJump(),
    trackingData: JumpTrackingData = generateSampleTrackingData(),
    onClick: () -> Unit = {}
) {


    Box(modifier = Modifier
        .background(Color.DarkGray)
        .height(350.dp)
        .fillMaxWidth()) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(skydive.staticMapUrl.replace("terrain", "satellite"))
                .crossfade(true)
                .build(),
            contentDescription = "barcode image",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClick() },
        )
    }
}

fun generateStaticMapsUrl(
    context : Context,
//    trackingData : JumpTrackingData,
    points : List<SkydiveDataPoint>,
    baseUrl : String = context.getString(R.string.maps_base_url),
    size : String = "${context.resources.getInteger(R.integer.maps_width)}x${context.resources.getInteger(R.integer.maps_height)}",
    zoom : Int = context.resources.getInteger(R.integer.maps_zoom),
    type : String = context.resources.getString(R.string.maps_type),
    key : String = GOOGLE_MAPS_API_KEY
    ) : String{


    points.filterByPhase(SkydivePhase.WALKING)


    val centreUrl = context.getString(R.string.maps_center_url_prepend) + points.centerPoint().latitude
    val zoomUrl = ""
    val sizeUrl = context.getString(R.string.maps_size_url_prepend) + size
    val mapTypeUrl = context.getString(R.string.maps_type_url_prepend) + type
    val keyUrl = context.getString(R.string.maps_key_url_prepend) + key
    return  baseUrl  + sizeUrl + zoomUrl + mapTypeUrl +
            pathToString(trackingPoints = points.filterByPhase(SkydivePhase.WALKING), PolylineSettings.WalkingPolyLineSettings) +
            pathToString(trackingPoints = points.filterByPhase(SkydivePhase.AIRCRAFT), PolylineSettings.AircraftPolyLineSettings) +
            pathToString(trackingPoints = points.filterByPhase(SkydivePhase.FREEFALL), PolylineSettings.FreefallPolyLineSettings) +
            pathToString(trackingPoints = points.filterByPhase(SkydivePhase.CANOPY), PolylineSettings.CanopyPolyLineSettings) +
            pathToString(trackingPoints = points.filterByPhase(SkydivePhase.LANDED), PolylineSettings.LandedPolyLineSettings) +
            keyUrl
}


