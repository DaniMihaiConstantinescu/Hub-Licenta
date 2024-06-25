package com.example.hubwifiv2.ui.icons

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.example.hubwifiv2.utils.icons.IconsIds

@Composable
fun RenderDeviceIcon(deviceType: String){

    when (deviceType){
        "ac" -> Icon(
            painter = painterResource(IconsIds.AC.drawableResId),
            contentDescription = IconsIds.AC.iconName
        )
        "dehumifier" -> Icon(
            painter = painterResource(IconsIds.DEHUIDIFIER.drawableResId),
            contentDescription = IconsIds.DEHUIDIFIER.iconName
        )
        "light" -> Icon(
            painter = painterResource(IconsIds.LIGHT.drawableResId),
            contentDescription = IconsIds.DEHUIDIFIER.iconName
        )
        "shutter" -> Icon(
            painter = painterResource(IconsIds.SHUTTER.drawableResId),
            contentDescription = IconsIds.SHUTTER.iconName
        )
    }

}