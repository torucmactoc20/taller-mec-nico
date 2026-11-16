package com.programacion2.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.programacion2.controlador.form.ClienteForm;
import com.programacion2.dominio.Cliente;
import com.programacion2.servicio.ClienteServicio;
import com.programacion2.servicio.ReglaNegocioException;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteServicio servicio;

    public ClienteController(ClienteServicio servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String buscar, Model model) {
        model.addAttribute("clientes", servicio.buscar(buscar));
        model.addAttribute("buscar", buscar);
        return "clientes/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        prepararFormulario(model, new ClienteForm(), "Nuevo cliente", "/clientes");
        return "clientes/form";
    }

    @PostMapping
    public String crear(@ModelAttribute("form") ClienteForm form,
                        Model model,
                        RedirectAttributes redirect) {
        try {
            servicio.crear(form.getNombre(), form.getApellido(), form.getTelefono());
            redirect.addFlashAttribute("mensaje", "Cliente registrado correctamente.");
            return "redirect:/clientes";
        } catch (ReglaNegocioException e) {
            model.addAttribute("error", e.getMessage());
            prepararFormulario(model, form, "Nuevo cliente", "/clientes");
            return "clientes/form";
        }
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable int id, Model model) {
        model.addAttribute("cliente", servicio.obtener(id));
        return "clientes/detalle";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable int id, Model model) {
        Cliente cliente = servicio.obtener(id);

        ClienteForm form = new ClienteForm();
        form.setNombre(cliente.getNombre());
        form.setApellido(cliente.getApellido());
        form.setTelefono(cliente.getTelefono());

        prepararFormulario(model, form, "Modificar cliente", "/clientes/" + id);
        return "clientes/form";
    }

    @PostMapping("/{id}")
    public String modificar(@PathVariable int id,
                            @ModelAttribute("form") ClienteForm form,
                            Model model,
                            RedirectAttributes redirect) {
        try {
            servicio.modificar(id, form.getNombre(), form.getApellido(), form.getTelefono());
            redirect.addFlashAttribute("mensaje", "Cliente modificado correctamente.");
            return "redirect:/clientes";
        } catch (ReglaNegocioException e) {
            model.addAttribute("error", e.getMessage());
            prepararFormulario(model, form, "Modificar cliente", "/clientes/" + id);
            return "clientes/form";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable int id, RedirectAttributes redirect) {
        try {
            servicio.eliminar(id);
            redirect.addFlashAttribute("mensaje", "Cliente eliminado correctamente.");
        } catch (ReglaNegocioException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/clientes";
    }

    private void prepararFormulario(Model model, ClienteForm form, String titulo, String accion) {
        model.addAttribute("form", form);
        model.addAttribute("titulo", titulo);
        model.addAttribute("accion", accion);
    }
}
