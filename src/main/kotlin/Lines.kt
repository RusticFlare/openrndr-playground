import org.openrndr.UnfocusBehaviour.THROTTLE
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.gui.GUI
import org.openrndr.extra.noise.scatter
import org.openrndr.extra.olive.oliveProgram
import org.openrndr.extra.parameters.Description
import org.openrndr.extra.parameters.DoubleParameter
import org.openrndr.extra.parameters.IntParameter
import org.openrndr.extra.parameters.Vector2Parameter
import org.openrndr.extra.shapes.hobbyCurve
import org.openrndr.math.Matrix44
import org.openrndr.math.Vector2
import org.openrndr.math.Vector3
import org.openrndr.math.transforms.scale
import org.openrndr.math.transforms.translate
import kotlin.random.Random

fun main() = application {
    configure {
        width = 1000
        height = 1000
        unfocusBehaviour = THROTTLE
        title = "Lo and behold!"
    }

    oliveProgram {
        extend(Screenshots()) {
            contentScale = 5.0
        }

        val settings = @Description("Settings") object {
            @IntParameter("Random Seed", low = Int.MIN_VALUE, high = Int.MAX_VALUE, order = 0)
            var randomSeed: Int = 0

            @DoubleParameter("Placement Radius", low = 1.0, high = 500.0, order = 1)
            var placementRadius: Double = 50.0

            @Vector2Parameter("Letterbox", min = 0.0, max = 1000.0, order = 2)
            var letterbox: Vector2 = Vector2(x = 250.0, y = 750.0)
        }

        extend(GUI().apply { add(settings) })

        extend {
            drawer.clear(ColorRGBa.WHITE)

            drawer.bounds.scatter(placementRadius = settings.placementRadius, random = Random(settings.randomSeed))
            val s = hobbyCurve(
                points = drawer.bounds.scatter(
                    placementRadius = settings.placementRadius,
                    random = Random(settings.randomSeed)
                ),
                closed = true
            )

            drawer.fill = null
            drawer.stroke = ColorRGBa.PINK
            drawer.contour(s)
            drawer.contour(
                s
                    .transform(Matrix44.scale(Vector3(x = 0.5, y = 0.5, z = 0.0)))
                    .transform(Matrix44.translate(Vector3(x = 100.0, y = 100.0, z = 0.0)))
            )
        }
    }
}
