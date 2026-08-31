## 目标
按“有证据确认无用”清理当前仓库，避免把项目明确支持的可选业务模块误删。

## 将删除/清理的内容
1. 删除 `.github/workflows/yudao-ui-admin.yml`：该工作流固定使用不存在的 `yudao-ui-admin` 目录、`yarn.lock` 和构建脚本，当前仓库没有任何可运行的前端工程。
2. 从 `script/docker/docker-compose.yml` 移除失效的 `admin` 服务及其构建参数、端口和依赖关系；保留 MySQL、Redis、后端 server 服务。
3. 更新 `script/docker/Docker-HOWTO.md`，移除不存在的 `yudao-ui-admin` 文件树、前端构建说明和 admin UI 端口说明，使文档与剩余 Docker Compose 服务一致。
4. 删除 `yudao-ui/yudao-ui-admin-vue3/src/` 下仅有的 5 个 MES 前端残留文件：它们没有 package.json、入口、路由或依赖，且调用的 API 路径/字段已与后端不一致，无法构建或作为有效模块使用。
5. 删除 `.gitignore` 中已外置且当前不存在的 `/yudao-ui-app/unpackage/` 规则，清除旧前端目录残留引用。

## 明确保留
- 保留 `yudao-ui/*/README.md` 外部仓库指针，不把文档误当作模块代码。
- 保留 `yudao-module-member`、`bpm`、`report`、`mp`、`pay`、Mall、CRM、ERP、AI、IoT、MES、WMS、HRM、FMS、IM 等完整后端模块；它们虽默认未加入 reactor，但有真实生产代码、模块依赖和项目文档支持，不能仅凭注释状态判定无用。
- 保留 `yudao-dependencies`、`yudao-framework`、`yudao-server`、`yudao-module-system`、`yudao-module-infra` 及所有框架 starter、数据库脚本、测试基础设施和代码生成模板。

## 验证
1. 搜索仓库，确认不再有对 `yudao-ui-admin`、被删 MES 前端残片路径和 `yudao-ui-app/unpackage` 的失效引用；保留外部仓库文档链接及后端代码生成模板中的历史输出路径。
2. 执行 `git diff --check` 检查变更格式。
3. 执行 Maven 跳过测试的构建，确认当前有效 reactor 仍可编译打包。
4. 检查 `git status` 和变更统计，确保只包含上述清理范围，不包含 `.fastRequest/` 等既有未跟踪内容。