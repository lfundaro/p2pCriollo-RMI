JAVAC = javac
OPS =
LINK_OPS =
ID3LIB = jaudiotagger.jar
NANOXMLPATH = nanoxml.jar
#NANOXMLPATH = /net/raquella/ldc/redes/nanoxml/java/nanoxml-lite-2.2.3.jar
PARSER_OBJS = Song.java ParseXSPF.java ParseMP3dir.java
NODE_OBJS = $(PARSER_OBJS) P2pRequest.java P2pProtocolHandler.java ConsultThread.java P2pProtocol.java
CLIENT_OBJS = Song.java ConsultThread.java P2pProtocolHandler.java P2pRequest.java

use:
	echo -e 'Uso: make <cliente|nodo|all>'

all: node client

client: $(CLIENT_OBJS)
	$(JAVAC) cliente.java $(CLIENT_OBJS)

node: $(NODE_OBJS)
	$(JAVAC) nodo.java $(NODE_OBJS) -classpath $(NANOXMLPATH):$(ID3LIB)

clean:
	rm -rf *.class *.o Node Client parseXSPF

# parser: $(PARSER_OBJS)
# 	$(JAVAC) $(PARSER_OBJS)

# Song.class: Song.java
# 	$(JAVAC) $(OPS) Song.java

# ParseXSPF.class: ParseXSPF.java
# 	$(JAVAC) $(OPS) -classpath $(NANOXMLPATH) ParseXSPF.java Song.java

# Node.class: Node.java
# 	$(JAVAC) $(OPS) Node.java

# Client.class: Client.java
# 	$(JAVAC) $(OPS) Client.java

# ClientRequestThread.class: ClientRequestThread.java
# 	$(JAVAC) $(OPS) ClientRequestThread.java

# ServerRequest.class: ServerRequest.java
# 	$(JAVAC) $(OPS) ServerRequest.java

# P2pProtocolHandler.class: P2pProtocolHandler.java
# 	$(JAVAC) $(OPS) P2pProtocolHandler.java

# P2pRequest.class: P2pRequest.java
# 	$(JAVAC) $(OPS) P2pRequest.java

# P2pS.class: P2pS.java
# 	$(JAVAC) $(OPS) P2pS.java

# ConsultThread.class: ConsultThread.java
# 	$(JAVAC) $(OPS) ConsultThread.java
