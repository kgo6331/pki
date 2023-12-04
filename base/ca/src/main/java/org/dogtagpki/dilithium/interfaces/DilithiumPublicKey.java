package org.dogtagpki.dilithium.interfaces;

import org.dogtagpki.dilithium.impl.PolyVec;

import java.security.PublicKey;

public interface DilithiumPublicKey extends PublicKey {
	public byte[] getRho();
	public PolyVec getT1();
	public DilithiumParameterSpec getSpec();
}
