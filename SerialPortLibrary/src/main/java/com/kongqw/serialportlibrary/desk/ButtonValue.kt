package com.kongqw.serialportlibrary.desk

import android.util.Log

/**
 * Description:
 * Created by loctek on 2021/1/12.
 */
object ButtonValue {

    val specialBytes = arrayOf(
        0x77,
        0x3F,
        0x7C,
        0x5E,
        0x6D,
        0x54
    )

    @JvmStatic
    fun Byte2String(nByte: Byte): String? {
        val tString = Integer.toBinaryString(nByte.toInt())
        Log.d("二进制", tString)
        return tString.toString()
    }

    fun getIntValue(data: Byte): Int {
        return data.toInt() and 0xff
    }

    @JvmStatic
    fun getError(data1: Byte, data2: Byte, data3: Byte): String {
        var str2 = getStringValue(data2)
        if (str2 == "") {
            str2 = getSpecialStr1(data2)
        }

        var str3 = getStringValue(data3)
        if (str3 == "") {
            str3 = getSpecialStr1(data3)
        }
        return "E$str2$str3"
    }

    /**
     * 是否包含了小数点，二进制最高位是1的，去1，剩下的查表
     * 例：
     * EF = 1110 1111
     * 80 = 1000 0000
     * EF & 80 = 1000 0000
     *
     */
    @JvmStatic
    fun getNumber(data1: Byte, data2: Byte, data3: Byte): String {
        val hasDot = getIntValue(data2) and 0x80 == 0x80
        return if (hasDot) {
            val num2 = getIntValue(data2) and 0x7F // & 0111 1111 把最高位变0
            val dot = "."
            val num1Str = getNumberShow(data1)
            val num2Str = getNumberShow(num2.toByte())
            val num3Str = getNumberShow(data3)
            Log.i("getNumber", "$num1Str$num2Str$dot$num3Str")
            "$num1Str$num2Str$dot$num3Str"
        } else {
            Log.i(
                "getNumber",
                "${getNumberShow(data1)}${getNumberShow(data2)}${getNumberShow(data3)}"
            )
            "${getNumberShow(data1)}${getNumberShow(data2)}${getNumberShow(data3)}"

        }
    }


    @JvmStatic
    fun isOFF(data1: Byte, data2: Byte, data3: Byte): Boolean {
        if ((getSpecialStr1(data1) == "0" || getSpecialStr2(data1) == "O" || getStringValue(data1) == "O")
            && getStringValue(data2) == "F" && getStringValue(data3) == "F"
        ) {
            return true
        }
        return false
    }

    @JvmStatic
    fun isOL(data1: Byte, data2: Byte, data3: Byte): Boolean {
        if (getStringValue(data1) == "-"
            && (getSpecialStr1(data2) == "0" || getSpecialStr2(data2) == "O" || getStringValue(data2) == "O")
            && getStringValue(data3) == "L"
        ) {
            return true
        }
        return false
    }

    @JvmStatic
    fun isOn(data1: Byte, data2: Byte, data3: Byte): Boolean {
        if ((getSpecialStr1(data1) == "0" || getSpecialStr2(data1) == "O" || getStringValue(data1) == "O")
            && (getStringValue(data2) == "N" || getSpecialStr1(data2) == "N" || getSpecialStr2(data2) == "n")
            && data3 == 0x00.toByte()
        ) {
            return true
        }
        return false
    }

    /**
     * 78 5C 73
     */
    @JvmStatic
    fun isTop(data1: Byte, data2: Byte, data3: Byte): Boolean {
        if ((getSpecialStr1(data1) == "T" || getSpecialStr2(data1) == "T")
            && (getSpecialStr1(data2) == "0" || getSpecialStr2(data2) == "O" || getStringValue(data2) == "O")
            && getStringValue(data3) == "P"
        ) {
            return true
        }
        return false

//        if(data1 == 0x78.toByte() && data2 == 0x5C.toByte() && data3 == 0x73.toByte()){
//            return true
//        }
//        return false
    }

    /**
     * 7C 5C 78
     */
    @JvmStatic
    fun isBottom(data1: Byte, data2: Byte, data3: Byte): Boolean {
        if ((getSpecialStr2(data1) == "b" || getSpecialStr1(data1) == "B")
            && (getSpecialStr1(data2) == "0" || getSpecialStr2(data2) == "O" || getStringValue(data2) == "O")
            && (getSpecialStr1(data3) == "T" || getSpecialStr2(data3) == "T")
        ) {
            return true
        }
        return false
//        if (data1 == 0x7C.toByte() && data2 == 0x5C.toByte() && data3 == 0x78.toByte()) {
//            return true
//        }
//        return false
    }

    @JvmStatic
    fun isError(data1: Byte): Boolean {
        if (getStringValue(data1) == "E") {
            return true
        }
        return false
    }

    @JvmStatic
    fun isRST(data1: Byte, data2: Byte, data3: Byte): Boolean {
        if ((getSpecialStr2(data1) == "R" || getStringValue(data1) == "R")
            && (getSpecialStr1(data2) == "5" || getSpecialStr2(data2) == "S")
            && (getSpecialStr1(data3) == "T" || getSpecialStr2(data3) == "T")
        ) {
            return true
        }
        return false
    }

    @JvmStatic
    fun getSpecialStr1(data: Byte): String {
        return when (data) {
            0x77.toByte() -> "A"
            0x3F.toByte() -> "0"
            0x7C.toByte() -> "B"
            0x5E.toByte() -> "D"
            0x6D.toByte() -> "5"
            0x54.toByte() -> "N"
            0x78.toByte() -> "T"
            else -> ""
        }
    }

    @JvmStatic
    fun getSpecialStr2(data: Byte): String {
        return when (data) {
            0x77.toByte() -> "R"
            0x3F.toByte() -> "O"
            0x7C.toByte() -> "b"
            0x5E.toByte() -> "d"
            0x6D.toByte() -> "S"
            0x54.toByte() -> "n"
            0x31.toByte() -> "T"
            else -> ""
        }
    }

    @JvmStatic
    fun getNumberShow(data: Byte): String {
        return when (data) {
            0x3F.toByte() -> "0"
            0x06.toByte() -> "1"
            0x5B.toByte() -> "2"
            0x4F.toByte() -> "3"
            0x66.toByte() -> "4"
            0x6D.toByte() -> "5"
            0x7D.toByte() -> "6"
            0x07.toByte() -> "7"
            0x7F.toByte() -> "8"
            0x6F.toByte() -> "9"
            else -> {
                ""
            }
        }
    }

    /**
     *  0x77.toByte() -> "A"
     *  0x77.toByte() -> "R"
     *  0x3F.toByte() -> "0"
     *  0x3F.toByte() -> "O"
     *  0x7C.toByte() -> "B"
     *  0x7C.toByte() -> "b"
     *  0x5E.toByte() -> "D"
     *  0x5E.toByte() -> "d"
     *  0x6D.toByte() -> "5"
     *  0x6D.toByte() -> "S"
     *  0x54.toByte() -> "N"
     *  0x54.toByte() -> "n"
     */
    @JvmStatic
    fun getStringValue(data: Byte): String {
        return when (data) {
            0x40.toByte() -> "-"
            0x09.toByte() -> ":"
            0x80.toByte() -> "."
            0x58.toByte() -> "c"
            0x74.toByte() -> "h"
            0x06.toByte() -> "1"
            0x5B.toByte() -> "2"
            0x4F.toByte() -> "3"
            0x66.toByte() -> "4"
            0x7D.toByte() -> "6"
            0x07.toByte() -> "7"
            0x7F.toByte() -> "8"
            0x6F.toByte() -> "9"
            0x39.toByte() -> "C"
            0x79.toByte() -> "E"
            0x71.toByte() -> "F"
            0x3D.toByte() -> "G"
            0x76.toByte() -> "H"
            0x10.toByte() -> "I"
            0x1E.toByte() -> "J"
            0x7A.toByte() -> "K"
            0x38.toByte() -> "L"
            0x55.toByte() -> "M"
            0x37.toByte() -> "N"
            0x5C.toByte() -> "O"
            0x73.toByte() -> "P"
            0x67.toByte() -> "Q"
            0x50.toByte() -> "R"
            0x31.toByte() -> "T"
            0x3E.toByte() -> "U"
            else -> {
                ""
            }
        }
    }

}