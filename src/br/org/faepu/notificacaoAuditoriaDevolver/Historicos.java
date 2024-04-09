package br.org.faepu.notificacaoAuditoriaDevolver;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;

import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

public class Historicos {
	
	String msg;
	String obs;
	String nomeUsu;
	
	BigDecimal codParc;
	BigDecimal seq;
	
	Timestamp agora = new Timestamp(System.currentTimeMillis());
	
	public void InserirHistorico(BigDecimal usuarioLogado, BigDecimal nuFin, BigDecimal nuNota) throws Exception {
		
		JdbcWrapper JDBC = JapeFactory.getEntityFacade().getJdbcWrapper();
		NativeSql nativeSql = new NativeSql(JDBC);
		SessionHandle hnd = JapeSession.open();
		
		System.out.println("Entrou no metodo de Historico para inserção na tabela");
		
		ResultSet uso = nativeSql.executeQuery("SELECT NOMEUSU FROM TSIUSU WHERE CODUSU = " + usuarioLogado);
		
		while (uso.next()) {
			nomeUsu = uso.getString("NOMEUSU");
		}
		
		ResultSet rs = nativeSql.executeQuery("SELECT MAX(SEQ)+1 AS SEQ FROM AD_HISTORICOS");
		
		while (rs.next()) {
			seq = rs.getBigDecimal("SEQ");
			System.out.println(" SEq do historico :" + seq);
		}
		
		obs = "Devolução de Notificação da DIPED para usuario.";
		char[] obsChar = obs.toCharArray();
		
		
		try {
	
			JapeWrapper hisDAO = JapeFactory.dao("AD_HISTORICOS");
			DynamicVO savePar = hisDAO.create()
					.set("SEQ", seq)
					.set("NUCHAVE", nuFin)
					.set("TABELA", "NUFIN")
					.set("USUARIO", nomeUsu)
					.set("DATA", agora)
					.set("ACAO", "DEVOLUÇÃO DE NOTIFICAÇÃO DIPDE")
					.set("PROCESSO", "USUARIO DEVOLVEU A NOTIFICAÇÃO PARA AUDITORIA")
					.set("OBS",obsChar )
					.set("NUNOTA", nuNota).save();
			
		}  catch (Exception e) {
			e.printStackTrace();
			msg = "Erro na inclusao do item " + e.getMessage();
			System.out.println(msg);
		}
		
		JapeSession.close(hnd);
	}
}
