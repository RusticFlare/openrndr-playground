import org.openrndr.WindowMultisample
import org.openrndr.application
import org.openrndr.draw.DrawPrimitive
import org.openrndr.draw.loadImage
import org.openrndr.draw.shadeStyle
import org.openrndr.extra.camera.Orbital
import org.openrndr.extra.meshgenerators.boxMesh
import org.openrndr.extra.olive.oliveProgram
import org.openrndr.math.Vector3

fun main() = application {
    configure {
        width = 640
        height = 360
        multisample = WindowMultisample.SampleCount(2)
    }
    oliveProgram {
        val cube = boxMesh()
        val tex = loadImage("data/images/cheeta.jpg")
        val cam = Orbital()
        cam.eye = -Vector3.UNIT_Z * 150.0

        extend(cam)
        extend {
            drawer.shadeStyle = shadeStyle {
                fragmentTransform = "x_fill = texture(p_tex, va_texCoord0.xy);"
                parameter("tex", tex)
            }
            drawer.scale(90.0)
            drawer.vertexBuffer(cube, DrawPrimitive.TRIANGLES)
        }
    }
}