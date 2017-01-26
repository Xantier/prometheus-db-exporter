package com.hallila.jussi

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.companionObject


fun <T : Any> logger(forClass: Class<T>): Logger {
    return LoggerFactory.getLogger(unwrapCompanionClass(forClass).name)
}

fun <T : Any> unwrapCompanionClass(ofClass: Class<T>): Class<*> {
    return if (ofClass.enclosingClass != null && ofClass.enclosingClass.kotlin.companionObject?.java == ofClass) {
        ofClass.enclosingClass
    } else {
        ofClass
    }
}

interface Loggable

fun Loggable.logger(): Logger = logger(this.javaClass)
abstract class WithLogging : Loggable {
    val log = logger()
}
