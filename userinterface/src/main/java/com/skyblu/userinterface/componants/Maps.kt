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
import com.skyblu.models.jump.*
import com.skyblu.userinterface.BuildConfig.GOOGLE_MAPS_API_KEY
import com.skyblu.userinterface.R
import com.skyblu.userinterface.ui.theme.ThemeBlueTwoAlpha

@Preview
@Composable
fun JumpMap(
    jump : Jump = generateSampleJump()
){
    val trackingData = jump.trackingData
    val cameraPadding = 5
    val cameraPositionState : CameraPositionState = rememberCameraPositionState()


    GoogleMap(
        modifier = Modifier.fillMaxSize(),

        properties = MapProperties(
            isMyLocationEnabled = false,
            latLngBoundsForCameraTarget = jump.trackingData.getCameraBounds(),
            minZoomPreference = 10f

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
        CreatePolyLine(
            settings = PolylineSettings.LandedPolyLineSettings,
            points = landedLine
        )

    }
}

@Composable
fun LiveJumpMap(
    cameraPositionState: CameraPositionState,
    walkingPoints: MutableList<TrackingPoint>,
    aircraftPoints: MutableList<TrackingPoint>,
    freefallPoints: MutableList<TrackingPoint>,
    canopyPoints: MutableList<TrackingPoint>,
    landedPoints: MutableList<TrackingPoint>,
){

    val wPoints : MutableList<LatLng> = mutableListOf()
    val aPoints : MutableList<LatLng> = mutableListOf()
    val cPoints : MutableList<LatLng> = mutableListOf()
    val fPoints : MutableList<LatLng> = mutableListOf()
    val lPoints : MutableList<LatLng> = mutableListOf()

    for(point in walkingPoints){
        wPoints.add(LatLng(point.latitude, point.longitude))
    }
    for(point in aircraftPoints){
        aPoints.add(LatLng(point.latitude, point.longitude))
    }
    for(point in canopyPoints){
        cPoints.add(LatLng(point.latitude, point.longitude))
    }
    for(point in freefallPoints){
        fPoints.add(LatLng(point.latitude, point.longitude))
    }
    for(point in landedPoints){
        lPoints.add(LatLng(point.latitude, point.longitude))
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
        )
    ){

        CreatePolyLine(
            settings = PolylineSettings.CanopyPolyLineSettings,
            points = cPoints
        )
        CreatePolyLine(
            settings = PolylineSettings.WalkingPolyLineSettings,
            points = wPoints
        )
        CreatePolyLine(
            settings = PolylineSettings.AircraftPolyLineSettings,
            points = aPoints
        )
        CreatePolyLine(
            settings = PolylineSettings.FreefallPolyLineSettings,
            points = fPoints
        )
        CreatePolyLine(
            settings = PolylineSettings.LandedPolyLineSettings,
            points = lPoints
        )

    }
}

@Preview
@Composable
fun TrackMap(
    jump : Jump = generateSampleJump()
){
    val trackingData = jump.trackingData
    val cameraPadding = 5
    val cameraPositionState : CameraPositionState = rememberCameraPositionState()

    GoogleMap(
        modifier = Modifier.fillMaxSize(),

        properties = MapProperties(
            isMyLocationEnabled = false,
            latLngBoundsForCameraTarget = jump.trackingData.getCameraBounds(),
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
        CreatePolyLine(
            settings = PolylineSettings.LandedPolyLineSettings,
            points = landedLine
        )

    }
}

sealed class PolylineSettings(
    val color: Color,
    val hex : String = "0xFF0000",
    val width : Float = 5f,
    val pattern : List<PatternItem>? = null,
    @DrawableRes val cap : Int? = null
) {
    object WalkingPolyLineSettings : PolylineSettings(Color.Gray, hex = "0x808080FF" )
    object AircraftPolyLineSettings : PolylineSettings(ThemeBlueTwoAlpha, cap = R.drawable.blue_plane, hex = "0x8000A8FF")
    object FreefallPolyLineSettings : PolylineSettings(Color.Red, hex = "0xFF0000FF")
    object CanopyPolyLineSettings : PolylineSettings(Color.Magenta, hex = "0xFFFF00FF",  cap = R.drawable.parachute)
    object LandedPolyLineSettings : PolylineSettings(Color.Gray, hex =  "0x808080FF")
}


@Composable
private fun CreatePolyLine(settings : PolylineSettings, points : List<LatLng>) : Unit{
    var pCap : Cap = ButtCap()
    if(settings.cap != null){
        val bitmapDescriptorFactory = bitmapDescriptorFromVector(context = LocalContext.current,
            vectorResId = settings.cap
        )
        pCap = CustomCap(bitmapDescriptorFactory!!, 5f)
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

//COPY & PASTE
@Composable
private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
    return ContextCompat.getDrawable(context, vectorResId)?.run {
        setBounds(0, 0, intrinsicWidth, intrinsicHeight)
        val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
        draw(android.graphics.Canvas(bitmap))
        BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}

@Preview(showBackground = true)
@Composable
fun StaticGoogleMap(
    jump: Jump = generateSampleJump(),
    onClick: () -> Unit = {}
) {

    val trackingData = jump.trackingData
    val center = trackingData.getCenterPoint(trackingData.importantTrackingPoints())
    val zoom = 10
    val size = "1000x1000"
    val mapType = "terrain"
    val key = GOOGLE_MAPS_API_KEY

   val  baseurl = "https://maps.googleapis.com/maps/api/staticmap?"
   val centreUrl ="center=${trackingData.getCenterPoint(trackingData.importantTrackingPoints()).stringConvert()}"
   val zoomUrl = "&zoom=${zoom}"
   val sizeUrl = "&size=$size"
   val mapTypeUrl = "&maptype=$mapType"
   val keyUrl = "&key=$key"

    val fullUrl = (baseurl + sizeUrl + mapTypeUrl +
            pathToString(trackingPoints = trackingData.walkingTrackingPoints, PolylineSettings.WalkingPolyLineSettings)
            + pathToString(trackingPoints = trackingData.aircraftTrackingPoints, PolylineSettings.AircraftPolyLineSettings)
            + pathToString(trackingPoints = trackingData.freefallTrackingPoints, PolylineSettings.FreefallPolyLineSettings)
            + pathToString(trackingPoints = trackingData.canopyTrackingPoints, PolylineSettings.CanopyPolyLineSettings)
            + pathToString(trackingPoints = trackingData.landedTrackingPoints, PolylineSettings.LandedPolyLineSettings)
            + keyUrl)

    Box(modifier = Modifier
        .background(Color.DarkGray)
        .height(350.dp)
        .fillMaxWidth()) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(fullUrl)
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

fun pathToString(trackingPoints : List<TrackingPoint>, settings : PolylineSettings) : String {
    val stringPrepend = "&path=color:${settings.hex}|weight:1"
    var stringAppend = ""
    for (trackingPoint in trackingPoints) {
        val lat = trackingPoint.latitude
        val long = trackingPoint.longitude
        stringAppend += "|${lat},${long}"
    }
    return  stringPrepend + stringAppend
}

fun LatLng.stringConvert() : String{
    return "$latitude,$longitude"
}