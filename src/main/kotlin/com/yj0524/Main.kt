package com.yj0524

import io.github.monun.kommand.*
import io.github.monun.tap.util.updateFromGitHubMagically
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class Main : JavaPlugin() {

    override fun onEnable() {
        // Plugin startup logic
        getLogger().info("Plugin Enabled")

        updateFromGitHubMagically("yj0524", "KotlinPlugin", "KotlinPlugin.jar") {
            Bukkit.getConsoleSender().sendMessage("${ChatColor.RED}새로운 버전이 출시되었습니다! 다운로드를 시작합니다.")
            fileDownload()
            Bukkit.getConsoleSender().sendMessage("${ChatColor.GREEN}다운로드가 완료되었습니다! 서버를 리로드하면 플러그인이 자동으로 적용됩니다.")
        }

        kommandsLoad()

        server.scheduler.runTaskTimer(this, Runnable {
            server.onlinePlayers.forEach { player ->
                player.setPlayerListHeaderFooter("", "Ping : ${player.ping}ms\nServer Memory : " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024 + "MB / " + Runtime.getRuntime().maxMemory() / 1024 / 1024 + "MB")
            }
        }, 0, 1)
    }

    override fun onDisable() {
        // Plugin shutdown logic
        getLogger().info("Plugin Disabled")
    }

    fun kommandsLoad() {
        kommand {
            register("kotlinplugin", "kp") {
                requires {
                    isOp
                }
                executes {
                    sender.sendMessage("§cUsage : /kotlinplugin <help | info | memory | myinfo | userinfo | returnstring | returnint | classload | effects>")
                }
                then("help") {
                    executes {
                        sender.sendMessage("§a/kotlinplugin info")
                        sender.sendMessage("§a/kotlinplugin memory")
                        sender.sendMessage("§a/kotlinplugin myinfo")
                        sender.sendMessage("§a/kotlinplugin userinfo <player>")
                        sender.sendMessage("§a/kotlinplugin returnstring <string>")
                        sender.sendMessage("§a/kotlinplugin returnint <int>")
                        sender.sendMessage("§a/kotlinplugin classload <int> <string>")
                    }
                }
                then("info") {
                    executes {
                        sender.sendMessage("Plugin Name : " + pluginMeta.name)
                        sender.sendMessage("Plugin Version : " + pluginMeta.version)
                        sender.sendMessage("Plugin API Version : " + pluginMeta.apiVersion)
                    }
                }
                then("memory") {
                    executes {
                        sender.sendMessage("§aServer Total Memory : " + Runtime.getRuntime().maxMemory() / 1024 / 1024 + "MB")
                        sender.sendMessage("§aServer Used Memory : " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024 + "MB")
                    }
                }
                then("myinfo") {
                    requires {
                        isPlayer
                    }
                    executes {
                        player.sendMessage("Nickname : ${player.name}")
                        player.sendMessage("UUID : ${player.uniqueId}")
                        player.sendMessage("Ping : ${player.ping}ms")
                    }
                }
                then("userinfo") {
                    executes {
                        sender.sendMessage("§cUsage : /kotlinplugin userinfo <player>")
                    }
                    then("target" to player()) {
                        executes {
                            val target: Player by it
                            sender.sendMessage("Nickname : ${target.name}")
                            sender.sendMessage("UUID : ${target.uniqueId}")
                            sender.sendMessage("Ping : ${target.ping}ms")
                        }
                    }
                }
                then("returnstring") {
                    executes {
                        sender.sendMessage("§cUsage : /kotlinplugin returnstring <string>")
                    }
                    then("string" to string()) {
                        executes {
                            val string: String by it
                            sender.sendMessage(string)
                        }
                    }
                }
                then("returnint") {
                    executes {
                        sender.sendMessage("§cUsage : /kotlinplugin returnint <int>")
                    }
                    then("int" to int()) {
                        executes {
                            val int: Int by it
                            sender.sendMessage(int.toString())
                        }
                    }
                }
                then("classload") {
                    executes {
                        sender.sendMessage("§cUsage : /kotlinplugin classload <int> <string>")
                    }
                    then("int" to int(), "string" to string(), "gagong" to string()) {
                        executes {
                            val int: Int by it
                            val string: String by it
                            val gagong: String by it

                            val loadedInt = ClassLoad().loadInt(int)
                            val loadedString = ClassLoad().loadString(string)
                            val loadedGagong = ClassLoad().loadGagong(gagong)

                            sender.sendMessage("§aLoaded Int : $loadedInt")
                            sender.sendMessage("§aLoaded String : $loadedString")
                            sender.sendMessage("§a$loadedGagong")
                        }
                    }
                }
                then("effects") {
                    requires {
                        isPlayer
                    }
                    executes {
                        sender.sendMessage("§cUsage : /kotlinplugin effects <bool>")
                    }
                    then("effect" to bool()) {
                        executes {
                            val effect: Boolean by it
                            if (effect) {
                                player.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, -1, 255, true, false))
                                player.addPotionEffect(PotionEffect(PotionEffectType.SATURATION, -1, 255, true, false))
                                player.addPotionEffect(PotionEffect(PotionEffectType.NIGHT_VISION, -1, 255, true, false))
                                player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, -1, 255, true, false))
                                player.addPotionEffect(PotionEffect(PotionEffectType.FIRE_RESISTANCE, -1, 255, true, false))
                                player.sendMessage("§aEffects Enabled")
                            }
                            else if (!effect) {
                                player.removePotionEffect(PotionEffectType.REGENERATION)
                                player.removePotionEffect(PotionEffectType.SATURATION)
                                player.removePotionEffect(PotionEffectType.NIGHT_VISION)
                                player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE)
                                player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE)
                                player.sendMessage("§cEffects Disabled")
                            }
                        }
                    }
                }
                then("chat") {
                    executes {
                        sender.sendMessage("§cUsage : /kotlinplugin chat <bool>")
                    }
                    then("chat" to bool()) {
                        executes {
                            val chat: Boolean by it
                            if (chat) {
                                server.pluginManager.registerEvents(object : Listener {
                                    @EventHandler
                                    fun onChat(event: AsyncPlayerChatEvent) {
                                        event.isCancelled = false
                                    }
                                }, this@Main)
                                sender.sendMessage("§aChat Enabled")
                            }
                            else if (!chat) {
                                server.pluginManager.registerEvents(object : Listener {
                                    @EventHandler
                                    fun onChat(event: AsyncPlayerChatEvent) {
                                        event.isCancelled = true
                                    }
                                }, this@Main)
                                sender.sendMessage("§cChat Disabled")
                            }
                        }
                    }
                }
                then("pvp") {
                    executes {
                        sender.sendMessage("§cUsage : /kotlinplugin pvp <bool>")
                    }
                    then("pvp" to bool()) {
                        executes {
                            val pvp: Boolean by it
                            if (pvp) {
                                server.pluginManager.registerEvents(object : Listener {
                                    @EventHandler
                                    fun onPvP(event: EntityDamageByEntityEvent) {
                                        event.isCancelled = false
                                    }
                                }, this@Main)
                                sender.sendMessage("§aPvP Enabled")
                            }
                            else if (!pvp) {
                                server.pluginManager.registerEvents(object : Listener {
                                    @EventHandler
                                    fun onPvP(event: EntityDamageByEntityEvent) {
                                        event.isCancelled = true
                                    }
                                }, this@Main)
                                sender.sendMessage("§cPvP Disabled")
                            }
                        }
                    }
                }
            }
        }
    }

    fun fileDownload() {
        val fileUrl = "https://github.com/yj0524/KotlinPlugin/releases/latest/download/KotlinPlugin.jar"

        try {
            val url = URL(fileUrl)
            val httpConn = url.openConnection() as HttpURLConnection
            val responseCode = httpConn.responseCode

            // HTTP 응답 코드가 200인 경우 (성공)
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 로컬에 저장할 파일 경로 생성
                val filePath = "${dataFolder}/../update/KotlinPlugin.jar"

                // 파일 출력 스트림 열기
                val inputStream: InputStream = httpConn.inputStream
                val outputStream = FileOutputStream(filePath)

                // 버퍼 선언 및 초기화
                val buffer = ByteArray(4096)
                var bytesRead = -1

                // 파일 내용을 버퍼에 읽어서 로컬 파일에 쓰기
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }

                // 자원 해제
                outputStream.close()
                inputStream.close()
            }

            // 연결 종료
            httpConn.disconnect()

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}
