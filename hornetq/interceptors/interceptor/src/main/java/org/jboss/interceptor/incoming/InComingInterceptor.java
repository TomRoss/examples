/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jboss.interceptor.incoming;

import java.util.Set;
import java.util.logging.Logger;
import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.Interceptor;
import org.hornetq.core.protocol.core.ChannelHandler;
import org.hornetq.core.protocol.core.Packet;
import org.hornetq.core.protocol.core.ServerSessionPacketHandler;
import org.hornetq.core.protocol.core.impl.ChannelImpl;
import org.hornetq.core.protocol.core.impl.HornetQPacketHandler;
import org.hornetq.core.protocol.core.impl.RemotingConnectionImpl;
import org.hornetq.core.protocol.core.impl.wireformat.CreateSessionMessage;
import org.hornetq.core.protocol.core.impl.wireformat.SessionAcknowledgeMessage;
import org.hornetq.core.protocol.core.impl.wireformat.SessionSendMessage;
import org.hornetq.core.server.impl.ServerSessionImpl;
import org.hornetq.spi.core.protocol.RemotingConnection;

/**
 *
 * @author tomr
 */
public class InComingInterceptor implements Interceptor{
    private static final Logger log = Logger.getLogger(InComingInterceptor.class.getName());
    @Override
    public boolean intercept(Packet packet, RemotingConnection connection) throws HornetQException {
      
      log.info("InComming::Processing packet: " + packet.getClass().getName() + " that camme from " + connection.getRemoteAddress() +".");
      
      RemotingConnectionImpl impl = (RemotingConnectionImpl) connection;
      ChannelImpl channel = (ChannelImpl) impl.getChannel(packet.getChannelID(), -1);
      
      ChannelHandler ch = channel.getHandler();
      
      log.info("Packet handler class = " + channel.getHandler().getClass().getName());
      
      if (ch instanceof ServerSessionPacketHandler) {
      
          ServerSessionPacketHandler ssph = (ServerSessionPacketHandler) ch;
          
          ServerSessionImpl ss = (ServerSessionImpl)ssph.getSession();
          
          log.info("Session name = " + ss.getName());
          log.info("Session User = " + ss.getUsername());
          log.info("Target address = " + ss.getTargetAddresses().toString());
          log.info("Last message ID = " + ss.getLastSentMessageID(null));
          log.info("Meta data = " + ss.getMetaData(null));
      }
      
      if ( packet instanceof CreateSessionMessage){
          
         /* log.info("===== Create session message ========");
          
          CreateSessionMessage csm = (CreateSessionMessage) packet;
          
          log.info("User name = " + csm.getName());
          log.info("User password = " + csm.getPassword());
          log.info("Session channelID = " + csm.getSessionChannelID());
          log.info("Remote address = " + csm.getDefaultAddress());
          log.info("XA = " + csm.isXA());
         
          log.info("RemotingConnection: " + connection.getRemoteAddress() + " with client ID = " + connection.getID());
          
          log.info( "User Name = " + getUsername(packet, connection)); */
          
          
      } else if (packet instanceof SessionAcknowledgeMessage){
         
          /*log.info("===== Session Ack message ========");
          
          log.info("packet = " + packet.getClass().getName());
          
          SessionAcknowledgeMessage ackPacket = (SessionAcknowledgeMessage) packet;
          log.info("Message ID = " + ackPacket.getMessageID());
          log.info("User ID = " + ackPacket.getConsumerID());
          
          log.info("RemotingConnection: " + connection.getRemoteAddress() + " with client ID = " + connection.getID());
          
          RemotingConnectionImpl impl = (RemotingConnectionImpl) connection;
          
          ChannelImpl channel = (ChannelImpl) impl.getChannel(packet.getChannelID(), -1);
          
          log.info("Packet handler - " + channel.getHandler().getClass().getName());*/
          
      } else if ( packet instanceof SessionSendMessage){
          
          /*log.info("===== Session Send message ========");
          
          log.info("packet = " + packet.getClass().getName());
          
          SessionSendMessage sendPacket = (SessionSendMessage) packet;
          
          Set props = sendPacket.getMessage().getPropertyNames();
          
          log.info("Message properties = " + props.toString());
          log.info("Message ID = " + sendPacket.getMessage());
          
          log.info("RemotingConnection: " + connection.getRemoteAddress() + " with client ID = " + connection.getID()); */
      }
      
      
      return true;
    }
    
    public String getUsername(final Packet packet, final RemotingConnection connection)
   {
        RemotingConnectionImpl impl = (RemotingConnectionImpl) connection;
        ChannelImpl channel = (ChannelImpl) impl.getChannel(packet.getChannelID(), -1);
        
        log.info("Packet handler class = " + channel.getHandler().getClass().getName());
        
        ChannelHandler ch = channel.getHandler();
        
        if (ch instanceof HornetQPacketHandler ){
           
           HornetQPacketHandler hqPh = (HornetQPacketHandler) ch;
           
           //hqPh.
        }
        
        // ServerSessionPacketHandler sessionHandler = (ServerSessionPacketHandler) channel.getHandler();
        
        //return sessionHandler.getSession().getUsername();
        
        return "";
   }
    
}
