import org.openrndr.Program
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.noise.scatter
import org.openrndr.extra.olive.oliveProgram
import org.openrndr.extra.shapes.hobbyCurve
import org.openrndr.shape.Circle
import org.openrndr.shape.ShapeContour
import kotlin.math.min
import kotlin.random.Random

fun main() = application {
    configure {
        width = 1000
        height = 1000
        windowResizable = true
    }
    oliveProgram {
        val scale = 5.0
        extend(Screenshots()) {
            contentScale = scale
        }
        extend {
            drawer.clear(ColorRGBa.BLACK)

            fun Program.funCircle(
                scale: Double,
                pointCount: Int,
                random: Random,
                pointsPerSegment: Int
            ): ShapeContour {
                val circle = Circle(center = drawer.bounds.center, radius = min(drawer.width, drawer.height) * scale)
//            drawer.circle(circle)

                val points = circle.contour.equidistantPositions(pointCount = pointCount)
                    .map { Circle(center = it, radius = circle.contour.length / pointCount / 2.0) }
//                .onEach { drawer.circle(it) }
                    .flatMap {
                        it.scatter(placementRadius = 3.0, random = random).shuffled(random).take(pointsPerSegment)
                    }
//                .onEach { drawer.circle(it, radius = 1.0) }
                val contour = hobbyCurve(points, closed = true)
                drawer.contour(contour)
                return contour
            }

            drawer.stroke = null
            drawer.fill = ColorRGBa.WHITE
            val outer = funCircle(
                scale = 0.4,
                pointCount = 100,
                random = Random(seed = 236879567),
                pointsPerSegment = 10,
            )

            drawer.stroke = ColorRGBa.BLACK
            drawer.fill = null
            funCircle(
                scale = 0.30,
                pointCount = 50,
                random = Random(seed = 26007),
                pointsPerSegment = 4,
            )

            drawer.stroke = null
            drawer.fill = ColorRGBa.BLACK
            val inner = funCircle(
                scale = 0.20,
                pointCount = 40,
                random = Random(seed = 2359906984),
                pointsPerSegment = 4,
            )
        }
    }
}
