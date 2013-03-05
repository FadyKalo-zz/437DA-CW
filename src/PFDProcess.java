/**
 * Created with IntelliJ IDEA.
 * User: fady
 * Date: 04/03/2013
 * Time: 20:29
 * To change this template use File | Settings | File Templates.
 */
public class PFDProcess extends Process{
private IFailureDetector detector;

public PFDProcess(String name, int id, int size){
	super(name,id,size);
		detector = new PerfectFailureDetector(this);

}

public void begin(){
	detector.begin();
}

@Override
public synchronized void receive (Message m){
	String type =m.getType();
	if (type.equals("heartbeat")){
		detector.receive(m);
	}
}
public static void main(String [] args){
	String name=args[0];
	int id = Integer.parseInt(args[1]);
	int size = Integer.parseInt(args[2]);
	P p = new P(name,id,size);
	p.registeR();
	p.begin();
}
}
