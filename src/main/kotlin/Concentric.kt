import org.openrndr.UnfocusBehaviour.THROTTLE
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.gui.GUI
import org.openrndr.extra.noise.scatter
import org.openrndr.extra.olive.oliveProgram
import org.openrndr.extra.parameters.*
import org.openrndr.math.Vector2
import org.openrndr.shape.Circle
import org.openrndr.shape.Rectangle
import org.openrndr.shape.difference
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
            @ColorParameter("Color", order = 0)
            var color: ColorRGBa = ColorRGBa.PINK

            @DoubleParameter("Darken", low = 0.0, high = 1.0, order = 1)
            var darken: Double = 0.0

            @DoubleParameter("Lighten", low = 0.0, high = 1.0, order = 2)
            var lighten: Double = 0.0

            @ColorParameter(label = "Ball Color", order = 3)
            var ballColor: ColorRGBa = ColorRGBa.GRAY

            @DoubleParameter("Ball Radius", low = 1.0, high = 100.0, order = 4)
            var ballRadius: Double = 10.0

            @DoubleParameter("Ball Placement Radius", low = 1.0, high = 500.0, order = 5)
            var ballPlacementRadius: Double = 50.0

            @DoubleParameter("Distance Lighten", low = 0.0, high = 1.0, order = 6)
            var distanceLighten: Double = 0.0

            @DoubleParameter("Stroke Weight", low = 1.0, high = 100.0)
            var strokeWeight: Double = 1.0

            @DoubleParameter("Placement Radius", low = 1.0, high = 500.0)
            var placementRadius: Double = 50.0

            @DoubleParameter("Radius", low = 1.0, high = 500.0)
            var radius: Double = 50.0

            @IntParameter("Random Seed", low = Int.MIN_VALUE, high = Int.MAX_VALUE)
            var randomSeed: Int = 0

            @IntParameter("Circles", low = 1, high = 10)
            var circles: Int = 1

            @Vector2Parameter("Letterbox", min = 0.0, max = 1000.0)
            var letterbox: Vector2 = Vector2(x = 250.0, y = 750.0)
        }

        extend(GUI().apply { add(settings) })

        extend {
            drawer.clear(ColorRGBa.WHITE)
            drawer.strokeWeight = settings.strokeWeight
            val lightColor = settings.color.mix(other = ColorRGBa.WHITE, factor = settings.lighten)
            val darkColor = settings.color.mix(other = ColorRGBa.BLACK, factor = settings.darken)


            val random = Random(seed = settings.randomSeed)

            drawer.bounds.scatter(
                placementRadius = settings.placementRadius,
                random = random
            ).sortedBy { it.y }
                .forEach { center ->
                    val yRatio = (1 - (center.y / drawer.bounds.height)) * settings.distanceLighten
                    (settings.circles downTo 1)
                        .map { it.toDouble() / settings.circles }
                        .forEach { ratio ->
                            drawer.stroke = darkColor.mix(other = lightColor, factor = ratio)
                                .mix(ColorRGBa.WHITE, factor = yRatio)
                            val radius = settings.radius * ratio
                            drawer.circle(Circle(center = center, radius = radius))
                        }
                }

            drawer.stroke = null
            drawer.bounds.shape.scatter(placementRadius = settings.ballPlacementRadius, random = random)
                .forEach { center ->
                    val yRatio = (1 - (center.y / drawer.bounds.height)) * settings.distanceLighten
                    drawer.fill = settings.ballColor.mix(ColorRGBa.WHITE, factor = yRatio)
                        .copy(alpha = 0.6)
                    drawer.circle(Circle(center = center, radius = settings.ballRadius))
                }
            drawer.strokeWeight = 1.0
            drawer.stroke = settings.ballColor
            drawer.fill = ColorRGBa.WHITE
            val letterbox = Rectangle(
                drawer.bounds.center - settings.letterbox.div(scale = 2.0),
                width = settings.letterbox.x,
                height = settings.letterbox.y
            ).shape
            drawer.bounds.shape.difference(letterbox)
            drawer.shape(drawer.bounds.shape.difference(letterbox))
        }
    }
}
