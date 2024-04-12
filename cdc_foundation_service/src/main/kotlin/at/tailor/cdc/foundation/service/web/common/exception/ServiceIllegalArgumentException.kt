package at.tailor.cdc.foundation.service.web.common.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
class ServiceIllegalArgumentException : Exception()
