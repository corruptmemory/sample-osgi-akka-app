package com.typesafe.atmos.sample

import akka.actor._
import scala.concurrent.duration._
import akka.osgi.ActorSystemActivator
import org.osgi.framework.BundleContext

class Sample(system:ActorSystem) extends {
  val pingActor = system.actorOf(Props[PingActor], "pingActor")
  def run():Unit = {
    implicit val exec = system.dispatcher
    system.scheduler.schedule(0 seconds, 1 seconds, pingActor, Ping)
  }
  def shutdown():Unit = {
    system.stop(pingActor)
  }
}

case object Ping

class PingActor extends Actor {
  def receive = {
    case Ping => println("Pinged at: " + System.currentTimeMillis)
  }
}

class Activator extends ActorSystemActivator {
  var sample:Sample = _

  final private def shutdown():Unit = {
    if (sample != null) {
      sample.shutdown()
      sample = null
    }
  }

  def configure(context: BundleContext, system: ActorSystem) {
    // optionally register the ActorSystem in the OSGi Service Registry
    registerService(context, system)

    println("Starting com.typesafe.atmos.sample")
    sample = new Sample(system)
    sample.run()
  }

  override def stop(context:BundleContext):Unit = {
    println("Stopping com.typesafe.atmos.sample");
    shutdown()
    super.stop(context)
  }

}

