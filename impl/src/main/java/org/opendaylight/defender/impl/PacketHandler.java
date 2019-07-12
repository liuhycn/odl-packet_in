package org.opendaylight.defender.impl;

import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeConnectorId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeConnectorRef;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.service.rev130709.PacketProcessingListener;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.service.rev130709.PacketReceived;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PacketHandler implements PacketProcessingListener
{
	//private NotificationPublishService notificationPublishService;
    private static final Logger LOG = LoggerFactory.getLogger(PacketHandler.class);

	public PacketHandler()
	{
	    counter = 0;
		LOG.info("[liuhy] PacketHandler Initiated. ");
	}

    String srcIP, dstIP, ipProtocol, srcMac, dstMac;
    String stringEthType;
    int srcPort, dstPort;
    int counter;
    byte[] payload, srcMacRaw, dstMacRaw, srcIPRaw, dstIPRaw, rawIPProtocol, rawEthType, rawSrcPort, rawDstPort;


    NodeConnectorRef ingressNodeConnectorRef;
    // Ingress Switch Id
    NodeId ingressNodeId;
    // Ingress Switch Port Id from DataStore
    NodeConnectorId ingressNodeConnectorId;
    String ingressConnector, ingressNode;


	@Override
    public void onPacketReceived(PacketReceived notification)
    {
        // TODO Auto-generated method stub

        //LOG.info("[liuhy] enter 1 !!!!!");
        ingressNodeConnectorRef = notification.getIngress();
        ingressNodeConnectorId = InventoryUtility.getNodeConnectorId(ingressNodeConnectorRef);
        ingressConnector = ingressNodeConnectorId.getValue();
        ingressNodeId = InventoryUtility.getNodeId(ingressNodeConnectorRef);
        ingressNode = ingressNodeId.getValue();
        //LOG.info("[liuhy] enter 2 !!!!!");

        //packetSize = payload.length;

        //int packetSize = payload.length;

        payload = notification.getPayload();
        //LOG.info("[liuhy] enter 3 !!!!!");
        srcMacRaw = PacketParsing.extractSrcMac(payload);
        dstMacRaw = PacketParsing.extractDstMac(payload);
        srcMac = PacketParsing.rawMacToString(srcMacRaw);
        dstMac = PacketParsing.rawMacToString(dstMacRaw);

        rawEthType = PacketParsing.extractEtherType(payload);
        stringEthType = PacketParsing.rawEthTypeToString(rawEthType);

        if (dstMac.equals("FF:FF:FF:FF:FF:FF") && stringEthType.equals("806"))
        {
            LOG.info("[liuhy] This is an ARP packet ");
            LOG.info("[liuhy] Received packet from MAC {} to MAC {}, EtherType=0x{} ", srcMac, dstMac, stringEthType);

        }
        else
        {
            dstIPRaw = PacketParsing.extractDstIP(payload);
            srcIPRaw = PacketParsing.extractSrcIP(payload);
            dstIP = PacketParsing.rawIPToString(dstIPRaw);
            srcIP = PacketParsing.rawIPToString(srcIPRaw);

            rawIPProtocol = PacketParsing.extractIPProtocol(payload);
            ipProtocol = PacketParsing.rawIPProtoToString(rawIPProtocol).toString();


            rawSrcPort = PacketParsing.extractSrcPort(payload);
            srcPort = PacketParsing.rawPortToInteger(rawSrcPort);
            rawDstPort = PacketParsing.extractDstPort(payload);
            dstPort = PacketParsing.rawPortToInteger(rawDstPort);
        }

        counter = counter + 1;
        LOG.info("[liuhy] Totally receive {} packets for now ", counter);
    }

	
	
}