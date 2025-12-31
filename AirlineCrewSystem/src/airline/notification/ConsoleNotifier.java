package airline.notification;

public class ConsoleNotifier implements Notifier {
    
	@Override
    public void notify(String message) {
    	System.out.println(message);
    }
}
