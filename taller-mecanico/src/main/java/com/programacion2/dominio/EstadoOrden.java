package com.programacion2.dominio;

public enum EstadoOrden {

    ABIERTA("Abierta", "text-bg-warning"),
    EN_REPARACION("En reparación", "text-bg-primary"),
    FINALIZADA("Finalizada", "text-bg-success");

    private final String etiqueta;
    private final String claseCss;

    EstadoOrden(String etiqueta, String claseCss) {
        this.etiqueta = etiqueta;
        this.claseCss = claseCss;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public String getClaseCss() {
        return claseCss;
    }
}
