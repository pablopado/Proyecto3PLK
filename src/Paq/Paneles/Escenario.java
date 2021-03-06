package Paq.Paneles;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import Paq.BD.BaseDatos;
import Paq.Personajes.Bala;
import Paq.Personajes.Barril;
import Paq.Personajes.Enemigo;
import Paq.Personajes.Municion;
import Paq.Personajes.Prota;



public class Escenario extends JFrame{
	public static Prota miProta;
	public ArrayList<Enemigo> misEnemigos = new ArrayList<>();
	public ArrayList<Municion> T_Municion = new ArrayList<>();
	public ArrayList<Barril> barriles = new ArrayList<>();
	
	public static JPanel panelPrincipal;
	public JPanel panelPuntuacion;
	private Image ImagenFondo;
	private URL fondo;
	public static JTextField puntuacion;
	public JLabel etiquetapuntuacion;
	public static JTextField municion;
	public JLabel etiquetamunicion;
	public static JTextField ronda;
	public JLabel etiquetaronda;
	public static JTextField nombreJugador;
	public JLabel etiquetaNombre;
	public Menu menuprincipal;
	public static HiloMovPrsonaje hilomovimiento;
	public static HiloCrearZombis hilocrearZombis;
	public static HiloDisparar hilodeDisparo;
	public static HiloComprobarVidas hiloComprobarVidas;

	private static BaseDatos basedatos= new BaseDatos();
	
	int numeroZombisRonda=12;
	int numeroZombisActuales=0;

	//vidas
	public static JTextField vidas;
	public JLabel etiquetaVidas;
	int vidasProta;	

	public Escenario(Menu a){

		menuprincipal=a;
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());
		//No se podra cambiar el tama�o de la ventana
		this.setResizable(false); 

		//Codigo para centrar la ventana

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		Dimension d = gs[0].getDefaultConfiguration().getBounds().getSize();
		int x = (int) d.getWidth()/4;
		int y = (int) d.getHeight()/4;
		this.setBounds(4*x - 2*(x + 250) , 4*y - 2*(y + 200) , (1920 + 200) /2, (1080 + 400)/2);

		fondo = this.getClass().getResource("ImgEscenario.png");
		ImagenFondo = new ImageIcon(fondo).getImage();
		
		// Creo los paneles

		panelPrincipal = new JPanel() {
			public void paintComponent(Graphics g) {
				g.drawImage(ImagenFondo, 0, 0, getWidth(), getHeight(), this);
			}
		};
		this.add(panelPrincipal, BorderLayout.CENTER);
		panelPrincipal.setLayout(null);

		panelPuntuacion = new JPanel();
		panelPuntuacion.setBackground(Color.BLACK);
		this.add(panelPuntuacion, BorderLayout.NORTH);
		panelPuntuacion.setLayout(new FlowLayout());


		//Etiquetas
		etiquetapuntuacion = new JLabel("Puntuacion: ");
		etiquetamunicion = new JLabel("Municion: ");
		etiquetaronda = new JLabel("Ronda ");
		etiquetapuntuacion.setForeground(Color.RED);
		etiquetamunicion.setForeground(Color.RED);
		etiquetaronda.setForeground(Color.RED);
		etiquetaNombre = a.etiquetaNombre;
		etiquetaNombre.setForeground(Color.RED);
		etiquetaVidas = new JLabel("Vidas: ");
		etiquetaVidas.setForeground(Color.RED);

		//Cuadros de texto
		puntuacion = new JTextField("000000");
		puntuacion.setEditable(false);
		municion = new JTextField("15");
		municion.setEditable(false);
		ronda = new JTextField("1");
		ronda.setEditable(false);
		nombreJugador = a.nombreJugador;
		nombreJugador.setEditable(false);
		puntuacion.setColumns(10);
		municion.setColumns(3);
		ronda.setColumns(3);
		vidas = new JTextField("000");
		vidas.setColumns(3);

		//A�adir a panel		
		panelPuntuacion.add(etiquetapuntuacion);
		panelPuntuacion.add(puntuacion);
		panelPuntuacion.add(etiquetaNombre);
		panelPuntuacion.add(nombreJugador);
		panelPuntuacion.add(etiquetamunicion);
		panelPuntuacion.add(municion);
		panelPuntuacion.add(etiquetaronda);
		panelPuntuacion.add(ronda);
		panelPuntuacion.add(etiquetaVidas);
		panelPuntuacion.add(vidas);



		// A�adido para que tambi�n se gestione por teclado con el KeyListener

		panelPrincipal.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
				miProta.keyReleased(arg0);

			}

			@Override
			public void keyPressed(KeyEvent arg0) {
				// TODO Auto-generated method stub
				miProta.keyPressed(arg0);


			}
		});


		panelPrincipal.setFocusable(true);
		panelPrincipal.requestFocus();
		panelPrincipal.addFocusListener( new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
			//	System.out.println("Foco: "+ Escenario.this.getFocusOwner());
				panelPrincipal.requestFocus();

			}
		});
		
		//Creacion de objetos
		creaPersonaje(500, 300);
		creaBarril(700,300);
		creaBarril(200,300);
		creaBarril(450,150);
		creaBarril(450,500);
		creaMunicion(400,300);
	}

	public static String getNombreJugador() {
		return  nombreJugador.getText();
	}
	public static String getPuntuacion() {
		return  puntuacion.getText();
	}
	/*Metodo para crear personaje
	 * @param posX Posicion en X
	 * @param posY Posicion en Y
	 */
	public void creaPersonaje( int posX, int posY ) {
		// Crear y a�adir el prota a la ventana
		miProta = new Prota(this);
		miProta.setPosicion( posX, posY );

		panelPrincipal.add( miProta.getGrafico()) ;  // A�ade al panel visual
		miProta.getGrafico().setLocation(posX, posY);
		miProta.getGrafico().repaint();  // Refresca el dibujado del prota
		//vidas(5)
		this.vidasProta = miProta.getVidas();

	}
	/*Metodo para crear barril
	 * @param posX Posicion en X
	 * @param posY Posicion en Y
	 */
	public void creaBarril( int posX, int posY ) {
		// Crear y a�adir el barril a la ventana
		Barril miBarril = new Barril(this);
		miBarril.setPosicion( posX, posY );
		barriles.add(miBarril);
		panelPrincipal.add( miBarril.getMiGrafico()) ; // A�ade al panel visual
		miBarril.getMiGrafico().setLocation(posX, posY);
		miBarril.getMiGrafico().repaint();  // Refresca el dibujado del barril
	}

	/*Metodo para crear municion
	 * @param posX Posicion en X
	 * @param posY Posicion en Y
	 */
	public void creaMunicion( int posX, int posY ) {
		// Crear y a�adir el barril a la ventana
		Municion miMunicion = new Municion(this);
		miMunicion.setPosicion( posX, posY );
		T_Municion.add( miMunicion);
		panelPrincipal.add( miMunicion.getMiGrafico() );  // A�ade al panel visual
		miMunicion.getMiGrafico().setLocation(posX, posY);
		miMunicion.getMiGrafico().repaint();  // Refresca el dibujado de la municion
	}

	/*Metodo para crear enemigos
	 * @param posX Posicion en X
	 * @param posY Posicion en Y
	 * @param n Numero actual de zombies
	 */
	public int creaEnemigo( int posX, int posY,int n) {
		// Crear y a�adir el enemigo a la ventana
		boolean p = true;
		Enemigo	miEnemigo = new Enemigo(this, miProta);
		miEnemigo.setPosicion( posX, posY );
		for(Enemigo otroEnemigo : misEnemigos){
			if(miEnemigo.hayChoqueconEnemigo(otroEnemigo) ){
				p=false;
			}else{

			}
		}
		if(p==true){
			misEnemigos.add(miEnemigo);
			panelPrincipal.add( miEnemigo.getGrafico()) ;  // A�ade al panel visual
			miEnemigo.getGrafico().setLocation(posX, posY);
			miEnemigo.getGrafico().repaint();  // Refresca el dibujado del prota
			return n+1;
		}
		return n;
	}


	public static void main(Menu a) {

		try {
			final Escenario escenario = new Escenario(a);

			//TODO Cambiar segun este localizado el archivo
			BaseDatos.initBD("C:\\Users\\USUARIO\\Desktop\\a\\git\\partida.bd");
			BaseDatos.crearTablaBD();
			escenario.setVisible( true );
			panelPrincipal.requestFocus();
			// Crea el hilo del juego
			escenario.hilomovimiento = escenario.new HiloMovPrsonaje();  // Sintaxis de new para clase interna
			Thread Hilomov = new Thread( escenario.hilomovimiento );
			Hilomov.start();
			escenario.hilocrearZombis= escenario.new HiloCrearZombis();  // Sintaxis de new para clase interna
			Thread HiloZombi = new Thread( escenario.hilocrearZombis );
			HiloZombi.start();
			escenario.hiloComprobarVidas = escenario.new HiloComprobarVidas();
			Thread Hilovid = new Thread(escenario.hiloComprobarVidas);
			Hilovid.start();
		} catch (Exception e) {
			System.exit(1);  // Error anormal
		}
	}
	//Hilo que comprueba si el prota ha muerto(si tiene 0 vidas)
	public class HiloComprobarVidas implements Runnable{
		boolean continuo = true;
		@Override
		public void run() {
			while(continuo){

				if(miProta.getVidas() <= 0){
					fin();
				}
			}
		}
		public void start() {
			// TODO Auto-generated method stub
			continuo= true;
		}
		/** Ordena al hilo detenerse en cuanto sea posible
		 */
		public boolean fin() {
			continuo = false;
			return true;
		}	

	}
	//Hilo que crea enemigos por rondas
	public class HiloCrearZombis implements Runnable {
		boolean sigo= true;
		@Override
		public void run() {
			// Bucle principal forever hasta que se pare el juego...
			while (sigo) {	
				//Lo que hara el hilo
				panelPrincipal.repaint();
				try {
					Thread.sleep( 2000 );
					double ran =  Math.random();
					if(ran>0.75){
						numeroZombisActuales=creaEnemigo(50, 300,	numeroZombisActuales);

					}else if(ran>0.5){
						numeroZombisActuales=creaEnemigo(50, 260,numeroZombisActuales);

					}else if(ran>0.25){
						numeroZombisActuales=creaEnemigo(950, 300,numeroZombisActuales);

					}else{
						numeroZombisActuales=creaEnemigo(950, 260,numeroZombisActuales);
						//
					}

				} catch (Exception e) {
				}
				if(numeroZombisActuales==numeroZombisRonda){
					boolean esperar = true;
					while(esperar){
						try {
							Thread.sleep( 2000 );
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if(misEnemigos.size()==0){
							Escenario.this.repaint();
							esperar = false;
						}
					}
					int p = Integer.parseInt(Escenario.this.ronda.getText())+1;
					Escenario.this.ronda.setText(Integer.toString(p));
					numeroZombisRonda=numeroZombisRonda+7;
					//Poner nueva ronda
				}
			}
		}
		public void start() {
			// TODO Auto-generated method stub
			sigo= true;
		}
		/** Ordena al hilo detenerse en cuanto sea posible
		 */
		public void acaba() {
			sigo = false;
		}
	}
	//Hilo que permite que el prota se mueva y con el que se acaba el juego
	public class HiloMovPrsonaje implements Runnable {
		boolean sigo= true;
		@Override
		public void run() {
			// Bucle principal forever hasta que se pare el juego...
			while (sigo) {	
				//Lo que hara el hilo
				panelPrincipal.repaint();
				//vidas(4)
				if(miProta != null){
					vidas.setText("" + miProta.getVidas());
					if(miProta.getVidas() <= 0){
						System.out.println("ACABA");
										Escenario.this.guardarBD();
						acaba();
						Escenario.hilocrearZombis.acaba();
						try {
							Thread.sleep( 1000 );
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Escenario.this.dispose();
						BaseDatos.close();
						PanelGameOver gameover = new PanelGameOver();
						gameover.setSize(1000, 600);
						gameover.setVisible( true );
					}
				}
				try {
					Thread.sleep( 100 );
					miProta.mover();
					for(Enemigo miEnemigo : misEnemigos){
						miEnemigo.mover();
					}

				} catch (Exception e) {
				}
			}
		}
		public void start() {
			// TODO Auto-generated method stub
			sigo= true;
		}
		/** Ordena al hilo detenerse en cuanto sea posible
		 */
		public void acaba() {
			sigo = false;
		}
	};
	
	//Hilo que permite disparar llamando al metodo disparo()
	public class HiloDisparar implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub

			try {
				System.out.println("pum");
				disparo();

			} catch (Exception e) {
			}

		}

	}
	//Metodo que genera balas cada vez que se pulsa la barra espaciadora
	private void disparo() {	
		double posicionx_bala;
		double posiciony_bala;
		if (miProta.getN_municion() > 0) {
			miProta.setN_municion(miProta.getN_municion()-1);
			Escenario.this.municion.setText(Integer.toString(miProta.getN_municion()));

			if (miProta.abajo) {
				Bala bala = new Bala(this,"balaABJ.png");
				posicionx_bala = miProta.getPosX();
				posiciony_bala = miProta.getPosY();
				bala.setPosicion( posicionx_bala , posiciony_bala );
				panelPrincipal.add(bala.getMiGrafico()); // A�ade al panel
				// visual
				bala.getMiGrafico().setLocation((int) posicionx_bala,
						(int) posiciony_bala);
				bala.getMiGrafico().repaint(); // Refresca el dibujado del prota
				bala.lanzamiento_disparo_abajo(this);
			} else if (miProta.arriba) {
				Bala bala = new Bala(this,"balaARR.png");
				posicionx_bala = miProta.getPosX();
				posiciony_bala =miProta.getPosY();
				bala.setPosicion( posicionx_bala , posiciony_bala );
				panelPrincipal.add(bala.getMiGrafico()); // A�ade al panel
				// visual
				bala.getMiGrafico().setLocation((int) posicionx_bala,
						(int) posiciony_bala);
				bala.getMiGrafico().repaint(); // Refresca el dibujado 
				bala.lanzamiento_disparo_arriba(this);
			} else if (miProta.derecha) {
				Bala bala = new Bala(this,"balaDER.png");
				posicionx_bala = miProta.getPosX();
				posiciony_bala = miProta.getPosY();
				bala.setPosicion( posicionx_bala , posiciony_bala );
				panelPrincipal.add(bala.getMiGrafico()); // A�ade al panel
				// visual
				bala.getMiGrafico().setLocation((int) posicionx_bala,
						(int) posiciony_bala);
				bala.getMiGrafico().repaint(); // Refresca el dibujado 
				bala.lanzamiento_disparo_derecha(this);
			} else if (miProta.izquierda) {
				Bala bala = new Bala(this,"balaIZQ.png");
				posicionx_bala = miProta.getPosX();
				posiciony_bala = miProta.getPosY();
				bala.setPosicion( posicionx_bala , posiciony_bala );
				panelPrincipal.add(bala.getMiGrafico()); // A�ade al panel
				// visual
				bala.getMiGrafico().setLocation((int) posicionx_bala,
						(int) posiciony_bala);
				bala.getMiGrafico().repaint(); // Refresca el dibujado
				bala.lanzamiento_disparo_izquierda(this);
			}
		}
	}
	
	//Metodo para guardar en la base de datos //TODO No funciona aun
	public void guardarBD(){

		String sql = "insert into prota values('"+getNombreJugador()+"','"+getPuntuacion()+"')";
		try {
			basedatos.
			getStatement().
			executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}