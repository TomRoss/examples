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

package org.jboss.interceptor.outgoing;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hornetq.api.core.HornetQBuffer;
import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.Interceptor;
import org.hornetq.api.core.Message;
import org.hornetq.api.core.SimpleString;
import org.hornetq.core.protocol.core.Packet;
import org.hornetq.core.protocol.core.impl.ChannelImpl;
import org.hornetq.core.protocol.core.impl.RemotingConnectionImpl;
import org.hornetq.core.protocol.core.impl.wireformat.SessionAcknowledgeMessage;
import org.hornetq.core.protocol.core.impl.wireformat.SessionReceiveMessage;
import org.hornetq.core.protocol.core.impl.wireformat.SessionSendMessage;
import org.hornetq.core.protocol.core.ServerSessionPacketHandler;
import org.hornetq.spi.core.protocol.RemotingConnection;

/**
 * A simple Interceptor implementation
 *
 * @author <a href="hgao@redhat.com">Howard Gao</a>
 */
public class OutGoingInterceptor implements Interceptor
{
    private static final Logger log = Logger.getLogger(OutGoingInterceptor.class.getName());

   public boolean intercept(final Packet packet, final RemotingConnection connection) throws HornetQException
   {
      SessionSendMessage realPacket = null;
      Message msg = null;
      
      log.info("OutGoing::Processing packet: " + packet.getClass().getName() + " that camme from " + connection.getRemoteAddress() +".");
      
      /* log.info("OutGoing::RemotingConnection: " + connection.getRemoteAddress() + " with client ID = " + connection.getID());

      if (packet instanceof SessionSendMessage)
      {
         realPacket = (SessionSendMessage)packet;

         msg = realPacket.getMessage();

         //String s = msg.getBodyBuffer().readNullableString();

         HornetQBuffer buf = msg.getBodyBuffer();

         buf.resetReaderIndex();

         SimpleString str = buf.readNullableSimpleString();

         log.info("The message is '" + str.toString() + "'.");
         
      } else if (packet instanceof SessionAcknowledgeMessage){
         
          log.info("packet = " + packet.getClass().getName());
          
          SessionAcknowledgeMessage ackPacket = (SessionAcknowledgeMessage) packet;
          log.info("Message ID = " + ackPacket.getMessageID());
          log.info("User ID = " + ackPacket.getConsumerID());
          
          
      } else if ( packet instanceof SessionSendMessage){
          
          SessionSendMessage sendPacket = (SessionSendMessage) packet;
          
          Set props = sendPacket.getMessage().getPropertyNames();
          
          log.info("Message properties = " + props.toString());
          log.info("Message ID = " + sendPacket.getMessage().getMessageID());
          
      } else if ( packet instanceof SessionReceiveMessage) {
       
          SessionReceiveMessage resPacket = (SessionReceiveMessage) packet;
          
          Set props = resPacket.getMessage().getPropertyNames();
          
          log.info("Message properties = " + props.toString());
          log.info("Message ID = " + resPacket.getMessage().getMessageID());
   }*/
       
   



      // We return true which means "call next interceptor" (if there is one) or target.
      // If we returned false, it means "abort call" - no more interceptors would be called and neither would
      // the target
      return true;
   }

   public String getUsername(final Packet packet, final RemotingConnection connection)
   {
        RemotingConnectionImpl impl = (RemotingConnectionImpl) connection;
        ChannelImpl channel = (ChannelImpl) impl.getChannel(packet.getChannelID(), -1);
        ServerSessionPacketHandler sessionHandler = (ServerSessionPacketHandler) channel.getHandler();
        
        return sessionHandler.getSession().getUsername();
   }

}
