import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.*
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.imageFit.FitMethod
import org.openrndr.extra.imageFit.fitRectangle

fun main() = application {
    configure {
        width = 1000
        height = 1000
        windowResizable = true
        windowTransparent = true
    }
    program {
        val scale = 5.0
        extend(Screenshots()) {
            contentScale = scale
        }
        val image = loadImage("data/images/beach-tower-2.png", ImageFileFormat.PNG)
        val at = arrayTexture(width = image.width, height = image.height, layers = 2)

        val rt = renderTarget(width = image.width, height = image.height) {
            arrayTexture(at, layer = 0)
        }

        extend {
//            drawer.fill = null
            drawer.clear(ColorRGBa.TRANSPARENT)
            val shadow = image.shadow.apply { download() }
            drawer.isolatedWithTarget(rt) {
                drawer.ortho(rt)
                drawer.stroke = null
                println(drawer.strokeWeight)
                for (x in 0 until image.width) {
                    (0 until image.height)
                        .asSequence()
                        .map { y -> shadow[x, y] }
                        .withIndex()
                        .onEach { (y, color) -> if (color.alpha != 1.0) shadow[x, y] = ColorRGBa.TRANSPARENT }
                        .zipWithNext()
                        .filter { (a, b) -> a.value.alpha == 1.0 && b.value.alpha != 1.0 }
                        .forEach { (a) ->
                            drawer.fill = a.value
                            drawer.rectangle(
                                x.toDouble(),
                                a.index.toDouble(),
                                1.0,
                                image.height.toDouble()
                            )
                        }
                }
            }
            shadow.upload()
            image.copyTo(at, layer = 1)

            val elements = fitRectangle(src = image.bounds, dest = drawer.bounds, fitMethod = FitMethod.Contain)
            drawer.image(
                at,
                layers = listOf(0, 1),
                rectangles = listOf(elements, elements)
            )
        }
    }
}