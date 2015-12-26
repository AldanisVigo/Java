import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JSlider;

import j.extensions.comm.*;
public class SerialServoControl {
	static SerialComm serialport;
	public static void main(String[] agrs){
		JFrame window = new JFrame();
		window.setLayout(new FlowLayout());
		window.setTitle("POT Position");
		window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		window.addWindowListener( new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent we) {
				System.exit(0);
			}
		});
		JSlider slider = new JSlider();
		slider.setMaximum(1023);
		
		
		SerialComm ports[] = SerialComm.getCommPorts();	//get a list of available serial ports
		if(ports[0] != null){
			serialport = ports[0];	//set the default port
		}
		
		
		System.out.println("Select a port:\n");	//Ask for a port
		int count = 1;
		String portStr[] = new String[100];
		for(SerialComm port : ports){
			System.out.println(count + ":" + port.getSystemPortName());
			portStr[count - 1] = port.getSystemPortName().toString();	//Extract the port names into a String array
			count++;
		}
		JComboBox<?> portList = new JComboBox<Object>(portStr);	//Create a combobox with that string array
		portList.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				JComboBox<?> cmbBox = (JComboBox<?>)evt.getSource(); 
				int portNumber = cmbBox.getSelectedIndex();
				serialport = ports[portNumber];
				
			}
		});
		JButton button = new JButton("Connect");
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt) {
				if(button.getText() == "Connect"){
					button.setText("Disconnect");
					window.pack();
			        if(serialport.openPort()){
						System.out.println("Port opened\n");
						
					}
					else{
						System.out.println("Error opening port.\n");
						return;
					}
					serialport.setComPortTimeouts(SerialComm.TIMEOUT_READ_SEMI_BLOCKING,0,0);
					@SuppressWarnings("resource")
					Scanner data = new Scanner(serialport.getInputStream());
					//System.out.println("Listening for data\n");
					while(data.hasNext()){
						try{
							System.out.println(data.next());
							
						}
						catch(Exception ex){
						}
					}
				}
				else if(button.getText() == "Disconnect"){
					serialport.closePort();
					System.out.println("Port closed.\n");
					button.setText("Connect");
					window.pack();
				}
			}
		});
		window.add(slider);
		window.add(portList);
		window.add(button);
		window.pack();
		window.setVisible(true);
	}
}
