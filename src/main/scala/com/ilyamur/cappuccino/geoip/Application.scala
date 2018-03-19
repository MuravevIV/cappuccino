package com.ilyamur.cappuccino.geoip

import java.util
import java.util.concurrent.ConcurrentSkipListMap

import org.apache.commons.lang3.StringUtils._

import scala.io.Source
import scala.util.{Success, Try}

object Application extends App {

  case class GeoIpData(country: String, countryCode: String)

  class GeoIpService {

    private val IPV4_FILE_PATH = "geoip/GeoLite2-Country-Blocks-IPv4.csv"
    private val LOCATIONS_FILE_PATH = "geoip/GeoLite2-Country-Locations-en.csv"

    private val P0_M = 256 * 256 * 256
    private val P1_M = 256 * 256
    private val P2_M = 256

    private val lookupMap = buildLookupMap()
    private val locationsMap = buildLocationsMap()

    def lookup(ip: String): Option[GeoIpData] = {
      val numIp = ipToNumIp(ip)
      val entry = lookupMap.floorEntry(numIp)
      locationsMap.get(entry.getValue).map { case (countryName, countryCode) =>
        GeoIpData(countryName, countryCode)
      }
    }

    private def ipToNumIp(ip: String): Long = {
      val arr = ip.split("\\.").map { part =>
        Try(part.toLong) match {
          case Success(value) => value
          case _ => throw new IllegalArgumentException(s"IP is malformed: '${ip}'")
        }
      }
      if (arr.length == 4) {
        arr(0) * P0_M + arr(1) * P1_M + arr(2) * P2_M + arr(3)
      } else {
        throw new IllegalArgumentException(s"IP is malformed: '${ip}'")
      }
    }

    private def buildLookupMap(): util.NavigableMap[Long, Int] = {
      val result = new ConcurrentSkipListMap[Long, Int]()
      Source.fromResource(IPV4_FILE_PATH)
        .getLines()
        .drop(1)
        .map { line =>
          val cells = line.split(",")
          (cells(0), Try(cells(1).toInt).getOrElse(-1))
        }
        .map {
          case (dirtyIp, geonameId) if dirtyIp.contains("/") =>
            (substringBefore(dirtyIp, "/"), geonameId)
          case cleanPair =>
            cleanPair
        }
        .map { case (ip, geonameId) =>
          (ipToNumIp(ip), geonameId)
        }
        .foreach { case (longIp, geonameId) =>
          result.put(longIp, geonameId)
        }
      result
    }

    private def buildLocationsMap(): Map[Long, (String, String)] = {
      Source.fromResource(LOCATIONS_FILE_PATH)
        .getLines()
        .drop(1)
        .map { line =>
          val cells = line.split(",")
          cells(0).toLong -> (cells(5), cells(4))
        }
        .toMap
    }
  }

  {
    val geoIpService = new GeoIpService()
    val geoIpData = geoIpService.lookup("178.66.153.184")
    println(geoIpData)
  }
}
