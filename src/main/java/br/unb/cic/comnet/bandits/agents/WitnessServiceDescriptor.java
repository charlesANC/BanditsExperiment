package br.unb.cic.comnet.bandits.agents;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class WitnessServiceDescriptor extends ServiceDescription {
	private static final long serialVersionUID = 1L;
	
	public static WitnessServiceDescriptor create(String localName) {
		WitnessServiceDescriptor service = new WitnessServiceDescriptor();
		service.setName(localName + "-" + service.getType());
		return service;
	}

	public static Set<AID> search(Agent client) throws FIPAException {
		DFAgentDescription template = new DFAgentDescription();
		template.addServices(new WitnessServiceDescriptor());
		
		DFAgentDescription[] result = DFService.search(client, template);
		
		return Arrays.asList(result).stream()
				.map(x->x.getName())
				.collect(Collectors.toSet());
	}

	private WitnessServiceDescriptor() {
		setType("Witness");
	}
}
