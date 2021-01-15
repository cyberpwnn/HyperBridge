package haus.man.hyperbridge;

import lombok.Data;

@Data
public class HyperBridgeState
{
	private String ipa;
	private String mac;
	private String usr;
	private long last;

	public HyperBridgeState()
	{
		
	}
}