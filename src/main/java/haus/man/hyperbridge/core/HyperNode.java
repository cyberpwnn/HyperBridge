package haus.man.hyperbridge;

import com.philips.lighting.model.PHBridge;

import lombok.Data;
import ninja.bytecode.shuriken.execution.Looper;
import ninja.bytecode.shuriken.execution.NastyRunnable;
import ninja.bytecode.shuriken.execution.Queue;
import ninja.bytecode.shuriken.execution.ShurikenQueue;
import ninja.bytecode.shuriken.logging.L;

@Data
public class HyperNode
{
	private Queue<NastyRunnable> queue;
	private PHBridge bridge;
	private Looper looper;

	public HyperNode(PHBridge bridge)
	{
		this.bridge = bridge;
		queue = new ShurikenQueue<NastyRunnable>().responsiveMode();
		looper = new Looper()
		{
			@Override
			protected long loop()
			{
				if(queue.hasNext())
				{
					try
					{
						queue.next().run();
					}

					catch(Throwable e)
					{
						L.ex(e);
					}
				}
				
				return 1000 / HyperConfig.get().getBridgeMaxLIOPS();
			}
		};

		looper.start();
	}

	public void clearQueue()
	{
		queue.clear();
	}

	public void close()
	{
		looper.interrupt();
	}

	public void queue(NastyRunnable r)
	{
		queue.queue(r);
	}
}
