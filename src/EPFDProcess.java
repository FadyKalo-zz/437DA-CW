/**
 * Created with IntelliJ IDEA.
 * User: fady
 * Date: 04/03/2013
 * Time: 20:27
 * To change this template use File | Settings | File Templates.
 */
public class EPFDProcess extends Process {

private IFailureDetector detector;

public EPFDProcess(String name, int id, int size){
	super(name,id,size);
//		detector = new PerfectFailureDetector(this);
	detector = new EventuallyPerfectFailureDetector(this);

}

public void begin(){
	detector.begin();
}
public synchronized void receive (Message m){
	Utils.out(pid, m.toString()); /* The default action. */
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