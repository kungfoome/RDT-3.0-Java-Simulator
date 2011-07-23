public class OSIRandom
{
    private long seed[] = new long[5];

    public OSIRandom(int s)
    {
	for (int i=0;i<5;i++)
	  seed[i] = (s+i) & 0xFFFFFFFFL;
    }

    public int nextInt(int i)
    {
	seed[i] = ((seed[i]&0xFFFFFFFFL)*(1103515245&0xFFFFFFFFL)+12345)&0xFFFFFFFFL;
	return (int)(seed[i]/65536)%32768;
    }

    public double nextDouble(int i)
    {
	return (double)nextInt(i)/32767;
    }
}
