package com.nutomic.ensichat.core.util

import java.util.concurrent.{CountDownLatch, TimeUnit}

import com.nutomic.ensichat.core.header.ContentHeader
import com.nutomic.ensichat.core.{Message, Address, MessageTest}
import junit.framework.TestCase
import org.joda.time.DateTime
import org.junit.Assert._

class MessageBufferTest extends TestCase {

  /**
    * MessageBuffer checks the time of a message, we have to use the current time or items might
    * time out.
    */
  private def adjustMessageTime(m: Message) =
    new Message(m.header.asInstanceOf[ContentHeader].copy(time=Some(DateTime.now)), m.body)

  val m1 = adjustMessageTime(MessageTest.m1)
  val m2 = adjustMessageTime(MessageTest.m2)

  def testGetMessages(): Unit = {
    val buffer = new MessageBuffer(Address.Null, () => _)
    buffer.addMessage(m1)
    buffer.addMessage(m2)
    val msgs = buffer.getMessages(m1.header.target)
    assertEquals(1, msgs.size)
    assertEquals(m1, msgs.head)
  }

  def testRetryMessage(): Unit = {
    val latch = new CountDownLatch(1)
    val buffer = new MessageBuffer(Address.Null, {e =>
      assertEquals(m1.header.target, e)
      latch.countDown()
    })
    buffer.addMessage(m1)
    assertTrue(latch.await(15, TimeUnit.SECONDS))
  }

}
