import org.openrndr.application
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.noise.scatter
import org.openrndr.extra.olive.oliveProgram
import org.openrndr.extra.shapes.hobbyCurve
import org.openrndr.shape.Shape
import org.openrndr.shape.ShapeContour
import kotlin.random.Random

/**
 *  This is a template for a live program.
 *
 *  It uses oliveProgram {} instead of program {}. All code inside the
 *  oliveProgram {} can be changed while the program is running.
 */
fun main() = application {
    configure {
        width = 1000
        height = 1000
    }

    oliveProgram {
        val scale = 5.0
        extend(Screenshots()) {
            contentScale = scale
        }
//        extend(ScreenRecorder()) {
//            maximumDuration = 10.0
//            contentScale = scale
//        }

        extend {
//            drawer.clear(ColorRGBa.PINK)
            drawer.stroke = null

            val placementRadius = 100.0
            val time = 0.01
            val keepAhead = 0.06
            val pointCount = 1

            val random = Random(seed = 454566)
            (0..100).map { random.nextInt() }
                .map { Random(it) }
                .map { drawer.bounds.scatter(placementRadius, random = it) }
                .map { hobbyCurve(it, closed = true) }
                .map { it.sub(seconds * time, seconds * time + keepAhead) }
                .map { it.equidistantPositions(pointCount) }
                .transpose()
                .map { Shape(listOf(ShapeContour.fromPoints(it, closed = true))) }
                .forEach { drawer.shape(it) }

        }
    }
}

fun <E> List<List<E>>.transpose(): List<List<E>> {
    // Helpers
    fun <E> List<E>.head(): E = this.first()
    fun <E> List<E>.tail(): List<E> = this.takeLast(this.size - 1)
    fun <E> E.append(xs: List<E>): List<E> = listOf(this).plus(xs)

    filter { it.isNotEmpty() }.let { ys ->
        return when (ys.isNotEmpty()) {
            true -> ys.map { it.head() }.append(ys.map { it.tail() }.transpose())
            else -> emptyList()
        }
    }
}
