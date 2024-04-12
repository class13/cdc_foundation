package at.tailor.cdc.foundation.service.web.category.service

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class ServiceModelNotFoundException: Exception()