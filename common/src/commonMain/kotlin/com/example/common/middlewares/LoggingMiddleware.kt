package com.example.common.middlewares

import com.example.common.state.AppState
import ru.pocketbyte.kydra.log.KydraLog
import org.reduxkotlin.Middleware
import ru.pocketbyte.kydra.log.info

val loggingMiddleware: Middleware<AppState> = { store ->
    { next ->
        { action ->
            KydraLog.info("***************************************")
            KydraLog.info("Action: ${action::class.simpleName}")
            KydraLog.info("***************************************")
            next(action)
        }
    }

}