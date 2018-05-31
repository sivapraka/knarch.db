package co.touchlab.kite.threads

import kotlinx.cinterop.COpaquePointer
import konan.internal.ExportForCppRuntime
import platform.Foundation.*
import konan.worker.*

// Object that holds the value we're sharing. The first two params are only there to fill out the space
// that Weak.cpp assumes will be there. This will likely change if the KN team decides to clean up
// their implementation.
internal class AtomicCounter(var referred: COpaquePointer? = null,
                             var lock: Int = 0,
                             var theData:Any? = null
                             ) {
}

fun <T> Atomic<T>.putValue(target:T){
    atomicGetCounter(this).theData = target
}



internal fun <T> Atomic<T>.runProc(proc:(T) -> Unit){
    val ac = atomicGetCounter(this)
    proc(ac.theData as T)
}

internal fun <T, W> Atomic<T>.runProcWith(proc:(T, W) -> Unit, data: W){
    val ac = atomicGetCounter(this)
    proc(ac.theData as T, data)
}

internal fun <T, R> Atomic<T>.runProcForResult(proc:(T) -> R):R{
    val ac = atomicGetCounter(this)
    return proc(ac.theData as T)
}

@SymbolName("Atomic_atomicGetCounter")
external internal fun atomicGetCounter(referent: Any):AtomicCounter

// Create a counter object.
@ExportForCppRuntime
internal fun makeAtomicCounter(target:Any?):AtomicCounter = AtomicCounter(theData = target)
