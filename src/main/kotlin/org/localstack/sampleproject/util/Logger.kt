package org.localstack.sampleproject.util

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

open class Logger {
    val LOGGER: Logger = LogManager.getLogger(javaClass.enclosingClass)
}