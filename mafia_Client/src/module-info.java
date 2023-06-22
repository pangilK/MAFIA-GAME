module project_mafia {
	requires javafx.controls;
	requires javafx.fxml;
	requires java.desktop;
	
	opens mafia to javafx.graphics, javafx.fxml,javafx.base,java.desktop;
}
