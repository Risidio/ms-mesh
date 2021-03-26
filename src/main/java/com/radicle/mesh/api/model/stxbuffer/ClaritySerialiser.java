package com.radicle.mesh.api.model.stxbuffer;

import java.math.BigInteger;
import java.nio.ByteBuffer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.TypeAlias;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@TypeAlias(value = "ClaritySerialiser")
public class ClaritySerialiser {

	@Autowired private ObjectMapper mapper;
	private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

	public String serialise(ClarityTypes type, BigInteger appCounter) {
	    
	    byte[] bytesValue = convertTwosCompliment(appCounter).toByteArray();
		ByteBuffer bufferValue = ByteBuffer.allocate(16);
		bufferValue.asIntBuffer();
		bufferValue.put(bytesValue);

	    byte[] bytesValue1 = appCounter.toByteArray();
		ByteBuffer bufferValue1 = ByteBuffer.allocate(16);
		bufferValue1.asIntBuffer();
		bufferValue1.put(bytesValue1);
		
		byte byteType = Integer.valueOf(type.ordinal()).byteValue();
		ByteBuffer bufferType = ByteBuffer.allocate(1);
		bufferType.put(byteType);
		bufferType.flip();
		
		ByteBuffer bufferCombo = combineBuffers(bufferType, bufferValue);
		String bufferComboString = asHex(combineBuffers(bufferType, bufferValue));
		ByteBuffer bufferCombo1 = combineBuffers(bufferType, bufferValue1);
		return ("0x" + asHex(bufferCombo1));
		//  return = Hex.encode(bufferCombo1.array(/* charset */));
	}
	
	private String asHex(ByteBuffer bb) {
		byte[] buf = bb.array();
	    char[] chars = new char[2 * buf.length];
	    for (int i = 0; i < buf.length; ++i)
	    {
	        chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
	        chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
	    }
	    return new String(chars);
    }

	public static BigInteger convertTwosCompliment(BigInteger appCounter) {
	    byte[] contents = appCounter.toByteArray();

	    // prepend byte of opposite sign
	    byte[] result = new byte[contents.length + 1];
	    System.arraycopy(contents, 0, result, 1, contents.length);
	    result[0] = (contents[0] < 0) ? 0 : (byte)-1;

	    // this will be two's complement
	    BigInteger result2 = new BigInteger(result);
	    return result2;
	}
	
	private ByteBuffer combineBuffers(ByteBuffer bufType, ByteBuffer bufValue) {
	    int length = bufType.limit() + bufValue.limit();
		ByteBuffer buffer = ByteBuffer.allocate(length);
		buffer.put(bufType);
		buffer.put(bufValue);
	    return buffer;
	}
}
