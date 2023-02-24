import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.loadFont
import org.openrndr.draw.loadImage
import org.openrndr.draw.tint
import kotlin.math.cos
import kotlin.math.sin

fun main() = application {
    val image = loadImage("data/images/mateus-guimaraes-uCqu7KVacG8-unsplash.jpg")
    configure {
        width = image.width
        height = image.height
    }

    program {

        extend {
            drawer.drawStyle.colorMatrix = tint(ColorRGBa.WHITE.shade(0.2))
            drawer.image(image)

            drawer.fill = ColorRGBa.PINK
            drawer.circle(cos(seconds) * width / 2.0 + width / 2.0, sin(0.5 * seconds) * height / 2.0 + height / 2.0, 140.0)

            drawer.fill = ColorRGBa.WHITE
            drawer.text("OPENRNDR", width / 2.0, height / 2.0)
        }
    }
}
