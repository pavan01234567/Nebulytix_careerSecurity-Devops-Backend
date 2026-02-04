package com.neb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.neb.util.ReportGeneratorPdf;

@Configuration
public class PdfConfig {

    @Bean
    public ReportGeneratorPdf reportGeneratorPdf() {
        return new ReportGeneratorPdf();
    }
}
