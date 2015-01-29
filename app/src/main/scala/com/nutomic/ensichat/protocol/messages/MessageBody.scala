package com.nutomic.ensichat.protocol.messages

/**
 * Holds the actual message content.
 */
abstract class MessageBody {

  def Type: Int

  /**
   * Writes the message contents to a byte array.
   */
  def write: Array[Byte]

  def length: Int

}