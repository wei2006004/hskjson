package com.vinson.common

import java.util.concurrent.*

/**
 * Created by Vinson on 2017/11/17.
 * e-mail: wei2006004@foxmail.com
 */

enum class ExecutorState {
    FREE, NORMAL, BUSY
}

fun ExecutorState.isFree() = this == ExecutorState.FREE

fun ExecutorState.isNormal() = this == ExecutorState.NORMAL

fun ExecutorState.isBusy() = this == ExecutorState.BUSY

open class MonitorThreadPool(val coreSize: Int, val maxSize: Int)
    : ThreadPoolExecutor(coreSize, maxSize, 1, TimeUnit.MINUTES, LinkedBlockingQueue(maxSize), ContinueRejectedExecutionHandler())

fun MonitorThreadPool.isFree() = state().isFree()

fun MonitorThreadPool.isNormal() = state().isNormal()

fun MonitorThreadPool.isBusy() = state().isBusy()

fun MonitorThreadPool.state(): ExecutorState {
    val active = activeCount
    val tasks = queue.size
    if (active <= maxSize * 0.6 && tasks < maxSize * 0.3) {
        return ExecutorState.FREE
    } else if (tasks < maxSize * 0.6) {
        return ExecutorState.NORMAL
    }
    return ExecutorState.BUSY
}

private val CPU_COUNT = 8

object IOExecutor : MonitorThreadPool(CPU_COUNT, CPU_COUNT * 3)

object ComputeExecutor : MonitorThreadPool(CPU_COUNT, CPU_COUNT * 2)

fun postIO(f: () -> Unit) = postToBackgroundThread(IOExecutor, f)

fun postBg(f: () -> Unit) = postToBackgroundThread(ComputeExecutor, f)

fun ioState() = IOExecutor.state()

fun bgState() = ComputeExecutor.state()

fun awaitBgState(postNum: Int) {
    awaitState(::bgState, postNum)
}

fun awaitIoState(postNum: Int) {
    awaitState(::ioState, postNum, listOf(10, 50, 300, 1000))
}

private fun awaitState(executorState: () -> ExecutorState, postNum: Int, sleeps : List<Long> = listOf(10, 20, 100, 300)) {
    if (postNum % CPU_COUNT == 0) {
        val state = executorState()
        if (state.isFree()) {
            Thread.sleep(sleeps[0])
            println("Free: $postNum")
        } else if (state.isNormal()) {
            Thread.sleep(sleeps[1])
            println("Normal: $postNum")
        } else {
            Thread.sleep(sleeps[2])
            println("Busy: $postNum")
            while (executorState().isBusy()) {
                Thread.sleep(sleeps[3])
                println("Still Busy: $postNum")
            }
        }
    }
}

fun postToBackgroundThread(executor: ExecutorService, f: () -> Unit) = postToBackgroundThread(executor, Runnable { f.invoke() })

fun postToBackgroundThread(executor: ExecutorService, runnable: Runnable) {
    executor.submit(runnable)
}

open class SimpleThread {
    val handler by lazy {
        Executors.newSingleThreadExecutor()
    }

    fun post(f: () -> Unit) = post(Runnable { f.invoke() })

    fun post(runnable: Runnable) = handler.execute(runnable)
}

private class ContinueRejectedExecutionHandler : RejectedExecutionHandler {
    object RejectedThread : SimpleThread()

    override fun rejectedExecution(r: Runnable, executor: ThreadPoolExecutor) {
        RejectedThread.post(r)
    }
}