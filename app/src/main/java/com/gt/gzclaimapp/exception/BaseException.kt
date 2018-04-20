package com.gt.gzclaimapp.exception

class BaseException(val code: Int, override val message: String) : Throwable()