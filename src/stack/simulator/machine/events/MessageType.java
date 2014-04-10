package stack.simulator.machine.events;

public enum MessageType {
	RALoad,
	RALoadResponse,
	RABlockingStore,
	RAStore,
	RAStoreNoAck,
	RABlockingStoreAck,
	RAAck,
	//RAStoreResponse
	CoreMiss,
	Eviction,
	Overflow,
	Underflow
}
