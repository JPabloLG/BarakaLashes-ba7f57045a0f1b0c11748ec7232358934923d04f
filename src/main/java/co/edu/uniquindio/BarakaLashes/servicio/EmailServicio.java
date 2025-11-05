package co.edu.uniquindio.BarakaLashes.servicio;

import co.edu.uniquindio.BarakaLashes.DTO.EmailDTO;

public interface EmailServicio {
    void sendMail(EmailDTO emailDTO) throws Exception;

}
