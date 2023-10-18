package com.visioncameraqrcode

import android.graphics.Point
import android.graphics.Rect
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.barcode.common.Barcode.CalendarDateTime
import com.google.mlkit.vision.barcode.common.Barcode.CalendarEvent
import com.google.mlkit.vision.barcode.common.Barcode.ContactInfo
import com.google.mlkit.vision.barcode.common.Barcode.DriverLicense
import com.google.mlkit.vision.barcode.common.Barcode.GeoPoint
import com.google.mlkit.vision.barcode.common.Barcode.PersonName
import com.google.mlkit.vision.barcode.common.Barcode.UrlBookmark
import com.google.mlkit.vision.barcode.common.Barcode.WiFi

/**
 * Converter util class used to convert Barcode related variables to either WritableNativeArray or
 * WritableNativeMap
 */
object BarcodeConverter {
  fun convertToArray(points: Array<Point>): List<Any> {
    val array: MutableList<Any> = ArrayList()
    for (point in points) {
      array.add(convertToMap(point))
    }
    return array
  }

  fun convertToArray(elements: Array<String>): List<Any> {
    val array: MutableList<Any> = ArrayList()
    for (elem in elements) {
      array.add(elem)
    }
    return array
  }

  fun convertStringList(elements: List<String>): List<Any> {
    val array: MutableList<Any> = ArrayList()
    for (elem in elements) {
      array.add(elem)
    }
    return array
  }

  fun convertAddressList(addresses: List<Barcode.Address>): List<Any> {
    val array: MutableList<Any> = ArrayList()
    for (address in addresses) {
      array.add(convertToMap(address))
    }
    return array
  }

  fun convertPhoneList(phones: List<Barcode.Phone>): List<Any> {
    val array: MutableList<Any> = ArrayList()
    for (phone in phones) {
      array.add(convertToMap(phone))
    }
    return array
  }

  fun convertEmailList(emails: List<Barcode.Email>): List<Any> {
    val array: MutableList<Any> = ArrayList()
    for (email in emails) {
      array.add(convertToMap(email))
    }
    return array
  }

  fun convertToMap(point: Point): Map<String, Any> {
    val map: MutableMap<String, Any> = HashMap()
    map["x"] = point.x
    map["y"] = point.y
    return map
  }

  fun convertToMap(address: Barcode.Address): Map<String, Any> {
    val map: MutableMap<String, Any> = HashMap()
    map["addressLines"] = convertToArray(address.addressLines)
    map["type"] = address.type
    return map
  }

  fun convertToMap(rect: Rect): Map<String, Any> {
    val map: MutableMap<String, Any> = HashMap()
    map["bottom"] = rect.bottom
    map["left"] = rect.left
    map["right"] = rect.right
    map["top"] = rect.top
    return map
  }

  fun convertToMap(contactInfo: ContactInfo): Map<String, Any?> {
    val map: MutableMap<String, Any?> = HashMap()
    map["addresses"] = convertAddressList(contactInfo.addresses)
    map["emails"] = convertEmailList(contactInfo.emails)
    map["name"] = convertToMap(contactInfo.name!!)
    map["organization"] = contactInfo.organization
    map["phones"] = convertPhoneList(contactInfo.phones)
    map["title"] = contactInfo.title
    map["urls"] = convertStringList(contactInfo.urls)
    return map
  }

  fun convertToMap(name: PersonName): Map<String, Any?> {
    val map: MutableMap<String, Any?> = HashMap()
    map["first"] = name.first
    map["formattedName"] = name.formattedName
    map["last"] = name.last
    map["middle"] = name.middle
    map["prefix"] = name.prefix
    map["pronunciation"] = name.pronunciation
    map["suffix"] = name.suffix
    return map
  }

  fun convertToMap(url: UrlBookmark): Map<String, Any?> {
    val map: MutableMap<String, Any?> = HashMap()
    map["title"] = url.title
    map["url"] = url.url
    return map
  }

  fun convertToMap(email: Barcode.Email): Map<String, Any?> {
    val map: MutableMap<String, Any?> = HashMap()
    map["address"] = email.address
    map["body"] = email.body
    map["subject"] = email.subject
    map["type"] = email.type
    return map
  }

  fun convertToMap(phone: Barcode.Phone): Map<String, Any?> {
    val map: MutableMap<String, Any?> = HashMap()
    map["number"] = phone.number
    map["type"] = phone.type
    return map
  }

  fun convertToMap(sms: Barcode.Sms): Map<String, Any?> {
    val map: MutableMap<String, Any?> = HashMap()
    map["message"] = sms.message
    map["phoneNumber"] = sms.phoneNumber
    return map
  }

  fun convertToMap(wifi: WiFi): Map<String, Any?> {
    val map: MutableMap<String, Any?> = HashMap()
    map["encryptionType"] = wifi.encryptionType
    map["password"] = wifi.password
    map["ssid"] = wifi.ssid
    return map
  }

  fun convertToMap(geoPoint: GeoPoint): Map<String, Any> {
    val map: MutableMap<String, Any> = HashMap()
    map["lat"] = geoPoint.lat
    map["lng"] = geoPoint.lng
    return map
  }

  fun convertToMap(calendarDateTime: CalendarDateTime): Map<String, Any?> {
    val map: MutableMap<String, Any?> = HashMap()
    map["day"] = calendarDateTime.day
    map["hours"] = calendarDateTime.hours
    map["minutes"] = calendarDateTime.minutes
    map["month"] = calendarDateTime.month
    map["rawValue"] = calendarDateTime.rawValue
    map["year"] = calendarDateTime.year
    map["seconds"] = calendarDateTime.seconds
    map["isUtc"] = calendarDateTime.isUtc
    return map
  }

  fun convertToMap(calendarEvent: CalendarEvent): Map<String, Any?> {
    val map: MutableMap<String, Any?> = HashMap()
    map["description"] = calendarEvent.description
    map["end"] = convertToMap(calendarEvent.end!!)
    map["location"] = calendarEvent.location
    map["organizer"] = calendarEvent.organizer
    map["start"] = convertToMap(calendarEvent.start!!)
    map["status"] = calendarEvent.status
    map["summary"] = calendarEvent.summary
    return map
  }

  fun convertToMap(driverLicense: DriverLicense): Map<String, Any?> {
    val map: MutableMap<String, Any?> = HashMap()
    map["addressCity"] = driverLicense.addressCity
    map["addressState"] = driverLicense.addressState
    map["addressStreet"] = driverLicense.addressStreet
    map["addressZip"] = driverLicense.addressZip
    map["birthDate"] = driverLicense.birthDate
    map["documentType"] = driverLicense.documentType
    map["expiryDate"] = driverLicense.expiryDate
    map["firstName"] = driverLicense.firstName
    map["gender"] = driverLicense.gender
    map["issueDate"] = driverLicense.issueDate
    map["issuingCountry"] = driverLicense.issuingCountry
    map["lastName"] = driverLicense.lastName
    map["licenseNumber"] = driverLicense.licenseNumber
    map["middleName"] = driverLicense.middleName
    return map
  }
}
