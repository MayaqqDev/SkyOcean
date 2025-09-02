package me.owdding.skyocean.features.misc

import com.mojang.blaze3d.platform.NativeImage

fun createFullShadow(image: NativeImage): NativeImage {
    val newImage = NativeImage(image.width + 2, image.height + 2, true)
    for (y in 0 until image.height) {
        for (x in 0 until image.width) {
            val newX = x + 1
            val newY = y + 1
            val pixel = image.getPixel(x, y)
            if (pixel != 0) {
                for (offsetY in -1 until 1) {
                    for (offsetX in -1 until 1) {
                        val finalX = newX + offsetX
                        val finalY = newY + offsetY
                        if (image.isOutsideBounds(finalX - 1, finalY - 1) || image.getPixel(finalX - 1, finalY - 1) != 0) {
                            newImage.setPixel(finalX, finalY, pixel)
                        }
                    }
                }
            }
        }
    }
    return newImage
}
