package com.programacion2.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.programacion2.controlador.form.OrdenForm;
import com.programacion2.dominio.EstadoOrden;
import com.programacion2.servicio.OrdenServicio;
import com.programacion2.servicio.ReglaNegocioException;
import com.programacion2.servicio.VehiculoServicio;

@Controller
@RequestMapping("/ordenes")
public class OrdenController {

    private final OrdenServicio servicio;
    private final VehiculoServicio vehiculoServicio;

    public OrdenController(OrdenServicio servicio, VehiculoServicio vehiculoServicio) {
        this.servicio = servicio;
        this.vehiculoServicio = vehiculoServicio;
    }

    @GetMapping
    public String listar(@RequestParam(name = "estado", required = false) EstadoOrden estado, Model model) {
        model.addAttribute("ordenes", servicio.listar(estado));
        model.addAttribute("estados", EstadoOrden.values());
        model.addAttribute("estadoSeleccionado", estado);
        return "ordenes/lista";
    }

    @GetMapping("/nueva")
    public String nueva(@RequestParam(name = "vehiculoId", required = false) Integer vehiculoId, Model model) {
        OrdenForm form = new OrdenForm();
        form.setVehiculoId(vehiculoId);
        prepararFormulario(model, form);
        return "ordenes/form";
    }

    @PostMapping
    public String abrir(@ModelAttribute("form") OrdenForm form,
                        BindingResult resultado,
                        Model model,
                        RedirectAttributes redirect) {
        try {
            if (resultado.hasErrors()) {
                throw new ReglaNegocioException("Revise los datos ingresados.");
            }
            servicio.abrir(form.getVehiculoId(), form.getDescripcion());
            redirect.addFlashAttribute("mensaje", "Orden de trabajo abierta correctamente.");
            return "redirect:/ordenes";
        } catch (ReglaNegocioException e) {
            model.addAttribute("error", e.getMessage());
            prepararFormulario(model, form);
            return "ordenes/form";
        }
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable("id") int id, Model model) {
        model.addAttribute("orden", servicio.obtener(id));
        return "ordenes/detalle";
    }

    @PostMapping("/{id}/iniciar")
    public String iniciar(@PathVariable("id") int id, RedirectAttributes redirect) {
        try {
            servicio.iniciarReparacion(id);
            redirect.addFlashAttribute("mensaje", "La orden pasó a estado En reparación.");
        } catch (ReglaNegocioException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ordenes/" + id;
    }

    @PostMapping("/{id}/finalizar")
    public String finalizar(@PathVariable("id") int id,
                            @RequestParam(name = "costo", required = false) Double costo,
                            RedirectAttributes redirect) {
        try {
            servicio.finalizar(id, costo);
            redirect.addFlashAttribute("mensaje", "La orden fue finalizada correctamente.");
        } catch (ReglaNegocioException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ordenes/" + id;
    }

    private void prepararFormulario(Model model, OrdenForm form) {
        model.addAttribute("form", form);
        model.addAttribute("vehiculos", vehiculoServicio.listar());
    }
}
