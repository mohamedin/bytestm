ByteSTM is a virtual machine-level Java STM implementation. ByteSTM implements two STM algorithms,
TL2 and RingSTM, and transparently supports implicit
transactions. Program bytecode is automatically modified
to support transactions. Being implemented at the VM-level, it accesses memory directly and uses absolute memory addresses to uniformly handle memory, and avoids Java
garbage collection by manually managing memory for transactional metadata. ByteSTM uses field-based granularity.