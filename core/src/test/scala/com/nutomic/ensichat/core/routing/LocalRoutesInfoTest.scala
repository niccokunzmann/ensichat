package com.nutomic.ensichat.core.routing

import com.nutomic.ensichat.core.routing
import junit.framework.TestCase
import org.joda.time.{DateTime, DateTimeUtils, Duration}
import org.junit.Assert._

class LocalRoutesInfoTest extends TestCase {

  private def connections() = Set(AddressTest.a1, AddressTest.a2)

  def testRoute(): Unit = {
    val routesInfo = new routing.LocalRoutesInfo(connections)
    routesInfo.addRoute(AddressTest.a3, 0, AddressTest.a1, 1)
    val route = routesInfo.getRoute(AddressTest.a3)
    assertEquals(AddressTest.a1, route.get.nextHop)
  }

  def testBestMetric(): Unit = {
    val routesInfo = new routing.LocalRoutesInfo(connections)
    routesInfo.addRoute(AddressTest.a3, 0, AddressTest.a1, 1)
    routesInfo.addRoute(AddressTest.a3, 0, AddressTest.a2, 2)
    val route = routesInfo.getRoute(AddressTest.a3)
    assertEquals(AddressTest.a1, route.get.nextHop)
  }

  def testConnectionClosed(): Unit = {
    val routesInfo = new routing.LocalRoutesInfo(connections)
    routesInfo.addRoute(AddressTest.a3, 0, AddressTest.a1, 1)
    routesInfo.addRoute(AddressTest.a4, 0, AddressTest.a1, 1)
    // Mark the route as active, because only active routes are returned.
    routesInfo.getRoute(AddressTest.a3)
    val unreachable = routesInfo.connectionClosed(AddressTest.a1)
    assertEquals(Set(AddressTest.a3), unreachable)
  }

  def testTimeout(): Unit = {
    DateTimeUtils.setCurrentMillisFixed(new DateTime().getMillis)
    val routesInfo = new routing.LocalRoutesInfo(connections)
    routesInfo.addRoute(AddressTest.a3, 0, AddressTest.a1, 1)
    DateTimeUtils.setCurrentMillisFixed(DateTime.now.plus(Duration.standardSeconds(400)).getMillis)
    assertEquals(None, routesInfo.getRoute(AddressTest.a3))
  }

  def testNeighbor(): Unit = {
    val routesInfo = new routing.LocalRoutesInfo(connections)
    val r1 = routesInfo.getRoute(AddressTest.a1)
    assertTrue(r1.isDefined)
    assertEquals(AddressTest.a1, r1.get.destination)
    assertEquals(1, r1.get.metric)
  }

  def testGetAllAvailableRoutes(): Unit = {
    val routesInfo = new routing.LocalRoutesInfo(connections)
    routesInfo.addRoute(AddressTest.a3, 0, AddressTest.a1, 1)
    val destinations = routesInfo.getAllAvailableRoutes.map(_.destination).toSet
    assertEquals(connections() + AddressTest.a3, destinations)

  }

}
