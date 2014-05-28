/*
 * Copyright 2009 Red Hat, Inc.
 * Red Hat licenses this file to you under the Apache License, version
 * 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */

package org.jboss.interceptor;

import java.nio.ByteBuffer;
import java.util.logging.Logger;
import org.hornetq.api.core.HornetQBuffer;
import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.Interceptor;
import org.hornetq.api.core.Message;
import org.hornetq.api.core.SimpleString;
import org.hornetq.core.protocol.core.Packet;
import org.hornetq.core.protocol.core.impl.PacketImpl;
import org.hornetq.core.protocol.core.impl.wireformat.SessionAcknowledgeMessage;
import org.hornetq.core.protocol.core.impl.wireformat.SessionReceiveMessage;
import org.hornetq.core.protocol.core.impl.wireformat.SessionSendMessage;
import org.hornetq.spi.core.protocol.RemotingConnection;

/**
 * A simple Interceptor implementation
 *
 * @author <a href="hgao@redhat.com">Howard Gao</a>
 */
public class SimpleInterceptor implements Interceptor
{
   private static final Logger LOG = Logger.getLogger(SimpleInterceptor.class.getName());

   public boolean intercept(final Packet packet, final RemotingConnection connection) throws HornetQException
   {
      LOG.info("SimpleInterceptor gets called!");
      LOG.info("Packet: " + packet.getClass().getName());
      LOG.info("RemotingConnection: " + connection.getRemoteAddress());

      if (packet instanceof SessionSendMessage)
      {
         SessionSendMessage realPacket = (SessionSendMessage)packet;
         
         Message msg = realPacket.getMessage();
         
         SimpleString adr = msg.getAddress();
         
         HornetQBuffer b = msg.getBodyBuffer();
         
         SimpleString str = msg.getBodyBuffer().readNullableSimpleString();
         
         LOG.info("b = " + str);
         
         msg.getBodyBuffer().getBytes(17, b);
         
         msg.getUserID();
         
         LOG.info("Address = " + adr.toString());
         
         Long id = msg.getMessageID();
         
         LOG.info("Message ID = " +id.toString());
         
         msg.putStringProperty(new SimpleString("newproperty"), new SimpleString("Hello from interceptor!"));
         
      } else if ( packet instanceof SessionReceiveMessage){
          
          SessionReceiveMessage realPacket = (SessionReceiveMessage) packet;
          
          Message msg = realPacket.getMessage();
          
          Long id = msg.getMessageID();
         
          LOG.info("Message ID = " +id.toString());
      } else if ( packet instanceof SessionAcknowledgeMessage){
          
          SessionAcknowledgeMessage realPacket = (SessionAcknowledgeMessage) packet;
          
          Long msgId = realPacket.getMessageID();
          
          LOG.info("Message ID = " + msgId);
          
      } else {
          
          PacketImpl realPacket = (PacketImpl) packet;
          
          byte packetType = realPacket.getType();
          
          LOG.info("Packet type =" + packetType + " Packet class = " + packet.getClass().getName());
          
      }
              
      // We return true which means "call next interceptor" (if there is one) or target.
      // If we returned false, it means "abort call" - no more interceptors would be called and neither would
      // the target
      return true;
   }

}
