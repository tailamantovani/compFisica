#include <SoftwareSerial.h>
#include <TinyGPS.h>
#include <AFMotor.h>

//lado esquerdo
AF_DCMotor Motor1(1);  //motor na porta M1
AF_DCMotor Motor2(2);  //motor na porta M2
//lado direito
AF_DCMotor Motor3(3);  //motor na porta M3
AF_DCMotor Motor4(4);  //motor na porta M4

char op;
int velocidade = 130;
int recebeu = 0;
int modo = 0;
char botao = 'p';

float latObj = -23.838588, lonObj = -52.306072;
float latAtual, lonAtual;

TinyGPS gps;
SoftwareSerial ss(5, 4);

void setup()
{
  Serial.begin(9600);
  ss.begin(9600);

  Serial.println("Inicializando...");

  //seta a velocidade dos motores para 0
  Motor1.setSpeed(0);
  Motor2.setSpeed(0);
  Motor3.setSpeed(0);
  Motor4.setSpeed(0);

}

void loop(){
  if(Serial.available()){
    modo = Serial.read(); 
  }
  Serial.println(modo);
  if(modo == 0){    //nenhuma opçao selecionada, realiza um caminho padrao
    percorreCaminho();    
  }
  else if(modo == '1'){  //modo coordenadas
    recebeu = recebeCoordenadas();
    while(recebeu != 1){
    }
    Serial.print("recebeu");
    loopGps();

  }
  else if(modo == '2'){  //modo percorre caminho
    if(Serial.available()){
      botao = Serial.read();
      while(botao != '0'){
        botao = Serial.read();
        comandaCaminho(botao);       
      }
    }
  }

}

int verificaModo(){
  if(Serial.available()){
    modo = Serial.read();
  }
}

int recebeCoordenadas(){
  //recebe as coordenadas do objetivo final
  if(Serial.available()){
    latObj = Serial.read();
    lonObj = Serial.read();
    return 1;
  } 
  else{
    return 0;
  }
}

void comandaCaminho(char b){
  if(b == 'f'){
    frente();
    delay(1000);
    parar();
  }
  else if(b == 'a'){
    atras();
    delay(1000);
    parar();
  }
  else if(b == 'e'){
    esquerda();
    delay(1000);
    parar();
  }
  else if(b == 'd'){
    direita();
    delay(1000);
    parar();
  }   
  else if(b == 'p'){
    parar();
    delay(1000);
  }
}

void loopGps(){
  bool newData = false;
  unsigned long chars;
  unsigned short sentences, failed;

  for (unsigned long start = millis(); millis() - start < 1000;)
  {
    while (ss.available())
    {
      char c = ss.read();
      if (gps.encode(c))
        newData = true;
    }
  }

  if (newData) {
    unsigned long age;
    gps.f_get_position(&latAtual, &lonAtual, &age);
    Serial.print("LAT=");
    Serial.print(latAtual == TinyGPS::GPS_INVALID_F_ANGLE ? 0.0 : latAtual, 6);
    Serial.print(" LON=");
    Serial.print(lonAtual == TinyGPS::GPS_INVALID_F_ANGLE ? 0.0 : lonAtual, 6);
    Serial.println();
  }

  gps.stats(&chars, &sentences, &failed);
  if (chars == 0)
    Serial.println("** Nenhum caracter recebido do GPS: verifique a conexao **");

  distancia();
}

void distancia(){
  float heading=0;
  float lat2 = latObj;
  float lon2 = lonObj;

  float lat1 = latAtual;
  float lon1 = lonAtual;

  float dist_calc=0;
  float dist_calc2=0;
  float diflat=0;
  float diflon=0;

  //Calcula distancia entre posiçao atual e a obj
  diflat=radians(lat2-lat1); 
  lat1=radians(lat1);
  lat2=radians(lat2);
  diflon=radians((lon2)-(lon1));

  dist_calc = (sin(diflat/2.0)*sin(diflat/2.0));
  dist_calc2= cos(lat1);
  dist_calc2*=cos(lat2);
  dist_calc2*=sin(diflon/2.0);                                       
  dist_calc2*=sin(diflon/2.0);
  dist_calc +=dist_calc2;

  dist_calc=(2*atan2(sqrt(dist_calc),sqrt(1.0-dist_calc)));

  dist_calc*=6371000.0; 


  lon1 = radians(lon1); 
  lon2 = radians(lon2);

  heading = atan2(sin(lon2-lon1)*cos(lat2),cos(lat1)*sin(lat2)-sin(lat1)*cos(lat2)*cos(lon2-lon1)),2*3.1415926535;
  heading = heading*180/3.1415926535;  
  int head =heading; 
  if(head<0){
    heading+=360;   
  }

  //-------------------------------------------------------------

  if(dist_calc<6){  //Percentual de erro adimitido
    parar();
    pronto();
  }

  int x4=heading;   
  float absolute= abs(x4);    
  if(absolute>180){
    absolute-=180;     
  }

  Serial.print("x4: ");
  Serial.println(x4);

  int x5;
  int turn = 0;

  if(x4 >= (-180)){
    if(x4<=0){
      turn=8;    //direita
    }
  }
  else if(x4 < (-180)){
    turn=5;      //esquerda
  }
  else if(x4>=0){
    if(x4<180){
      turn=5;   //esquerda
    }
  }
  else if(x4>=180){     //direita
    turn=8;
  }

  if(turn == 0){
    frente(); 
  }
  else if(turn == 8){
    direita(); 
  }
  else if(turn == 5){
    esquerda(); 
  }
}

void percorreCaminho(){
  frente();
  delay(1000);
  esquerda();
  delay(1000);
  atras();
  delay(1000);
  direita();
  delay(1000);
  parar();

}

void atras(){
  Motor1.setSpeed(velocidade);
  Motor2.setSpeed(velocidade);
  Motor3.setSpeed(velocidade);
  Motor4.setSpeed(velocidade);

  Motor1.run(FORWARD);
  Motor2.run(FORWARD);
  Motor3.run(FORWARD);
  Motor4.run(FORWARD);
}

void frente(){
  Motor1.setSpeed(velocidade);
  Motor2.setSpeed(velocidade);
  Motor3.setSpeed(velocidade);
  Motor4.setSpeed(velocidade);

  Motor1.run(BACKWARD);
  Motor2.run(BACKWARD);
  Motor3.run(BACKWARD);
  Motor4.run(BACKWARD);
}

void direita(){
  parar();
  delay(1000);

  Motor1.setSpeed(0);
  Motor2.setSpeed(0);
  Motor3.setSpeed(180);
  Motor4.setSpeed(180);

  Motor1.run(RELEASE);
  Motor2.run(RELEASE);
  Motor3.run(BACKWARD);
  Motor4.run(BACKWARD);

  delay(1000);
  parar();
}

void esquerda(){
  parar();
  delay(1000);

  Motor1.setSpeed(180);
  Motor2.setSpeed(180);
  Motor3.setSpeed(0);
  Motor4.setSpeed(0);

  Motor1.run(BACKWARD);
  Motor2.run(BACKWARD);
  Motor3.run(RELEASE);
  Motor4.run(RELEASE);

  delay(1000);
  parar();
}

void parar(){
  Motor1.setSpeed(0);
  Motor2.setSpeed(0);
  Motor3.setSpeed(0);
  Motor4.setSpeed(0);

  Motor1.run(RELEASE);
  Motor2.run(RELEASE);
  Motor3.run(RELEASE);
  Motor4.run(RELEASE);
}

void pronto(){
  while(true){
  } 
}





