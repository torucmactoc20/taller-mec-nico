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

import com.programacion2.controlador.form.VehiculoForm;
import com.programacion2.dominio.Cliente;
import com.programacion2.dominio.Vehiculo;
import com.programacion2.servicio.ClienteServicio;
import com.programacion2.servicio.ReglaNegocioException;
import com.programacion2.servicio.VehiculoServicio;

@Controller
@RequestMapping("/vehiculos")
public class VehiculoController {

    private final VehiculoServicio servicio;
    private final ClienteServicio clienteServicio;

    public VehiculoController(VehiculoServicio servicio, ClienteServicio clienteServicio) {
        this.servicio = servicio;
        this.clienteServicio = clienteServicio;
    }

    @GetMapping
    public String listar(@RequestParam(name = "patente", required = false) String patente, Model model) {
        model.addAttribute("vehiculos", servicio.buscar(patente));
        model.addAttribute("patente", patente);
        return "vehiculos/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(@RequestParam(name = "clienteId", required = false) Integer clienteId, Model model) {
        VehiculoForm form = new VehiculoForm();
        form.setClienteId(clienteId);
        prepararFormulario(model, form, "Nuevo vehículo", "/vehiculos", null);
        return "vehiculos/form";
    }

    @PostMapping
    public String crear(@ModelAttribute("form") VehiculoForm form,
                        BindingResult resultado,
                        Model model,
                        RedirectAttributes redirect) {
        try {
            if (resultado.hasErrors()) {
                throw new ReglaNegocioException("Revise los datos ingresados: el año debe ser un número.");
            }
            servicio.crear(form.getClienteId(), form.getPatente(), form.getMarca(),
                    form.getModelo(), form.getAnio());
            redirect.addFlashAttribute("mensaje", "Vehículo registrado correctamente.");
            return "redirect:/vehiculos";
        } catch (ReglaNegocioException e) {
            model.addAttribute("error", e.getMessage());
            prepararFormulario(model, form, "Nuevo vehículo", "/vehiculos", null);
            return "vehiculos/form";
        }
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable("id") int id, Model model) {
        model.addAttribute("vehiculo", servicio.obtener(id));
        return "vehiculos/detalle";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable("id") int id, Model model) {
        Vehiculo vehiculo = servicio.obtener(id);

        VehiculoForm form = new VehiculoForm();
        form.setPatente(vehiculo.getPatente());
        form.setMarca(vehiculo.getMarca());
        form.setModelo(vehiculo.getModelo());
        form.setAnio(vehiculo.getAnio());

        prepararFormulario(model, form, "Modificar vehículo", "/vehiculos/" + id, vehiculo.getCliente());
        return "vehiculos/form";
    }

    @PostMapping("/{id}")
    public String modificar(@PathVariable("id") int id,
                            @ModelAttribute("form") VehiculoForm form,
                            BindingResult resultado,
                            Model model,
                            RedirectAttributes redirect) {
        try {
            if (resultado.hasErrors()) {
                throw new ReglaNegocioException("Revise los datos ingresados: el año debe ser un número.");
            }
            servicio.modificar(id, form.getPatente(), form.getMarca(), form.getModelo(), form.getAnio());
            redirect.addFlashAttribute("mensaje", "Vehículo modificado correctamente.");
            return "redirect:/vehiculos/" + id;
        } catch (ReglaNegocioException e) {
            model.addAttribute("error", e.getMessage());
            prepararFormulario(model, form, "Modificar vehículo", "/vehiculos/" + id,
                    servicio.obtener(id).getCliente());
            return "vehiculos/form";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable("id") int id, RedirectAttributes redirect) {
        try {
            servicio.eliminar(id);
            redirect.addFlashAttribute("mensaje", "Vehículo eliminado correctamente.");
            return "redirect:/vehiculos";
        } catch (ReglaNegocioException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/vehiculos/" + id;
        }
    }

    /* cliente != null significa que se esta modificando (el dueño no se puede cambiar). */
    private void prepararFormulario(Model model, VehiculoForm form, String titulo,
                                    String accion, Cliente cliente) {
        model.addAttribute("form", form);
        model.addAttribute("titulo", titulo);
        model.addAttribute("accion", accion);
        model.addAttribute("editando", cliente != null);
        model.addAttribute("cliente", cliente);
        model.addAttribute("clientes", clienteServicio.listar());
    }
}
