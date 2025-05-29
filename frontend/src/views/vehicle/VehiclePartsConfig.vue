<template>
  <div class="vehicle-parts-config">
    <el-container>
      <!-- 左侧树形面板 -->
      <el-aside width="300px" class="tree-panel">
        <div class="tree-header">
          <h3>车辆配件树</h3>
          <el-button type="primary" size="small" @click="refreshTree" :loading="loading">
            <el-icon><Refresh /></el-icon>
          </el-button>
        </div>
        
        <el-skeleton :loading="loading" animated :count="3" v-if="loading">
          <template #template>
            <div style="padding: 10px">
              <el-skeleton-item variant="text" style="width: 100%; height: 20px; margin-bottom: 10px" />
              <el-skeleton-item variant="text" style="width: 90%; height: 20px; margin-left: 20px; margin-bottom: 10px" />
              <el-skeleton-item variant="text" style="width: 80%; height: 20px; margin-left: 40px; margin-bottom: 10px" />
            </div>
          </template>
        </el-skeleton>
        
        <el-tree
          v-else
          ref="treeRef"
          :data="treeData"
          :props="defaultProps"
          node-key="id"
          :expand-on-click-node="false"
          @node-contextmenu="handleNodeRightClick"
          @node-click="handleNodeClick"
          highlight-current
        >
          <template #default="{ node, data }">
            <span class="custom-tree-node">
              <el-icon v-if="data.type === 'vehicle'"><Van /></el-icon>
              <el-icon v-else><SetUp /></el-icon>
              <span>{{ node.label }}</span>
              <span v-if="data.typeName" class="part-type">({{ data.typeName }})</span>
            </span>
          </template>
        </el-tree>

        <!-- 右键菜单 -->
        <div
          v-show="contextMenuVisible"
          :style="contextMenuStyle"
          class="context-menu"
          @mouseleave="hideContextMenu"
        >
          <ul>
            <li @click="handleAddPart">添加配件</li>
          </ul>
        </div>
      </el-aside>

      <!-- 右侧面板 -->
      <el-main class="form-panel">
        <!-- 空状态 -->
        <div v-if="!showAddForm && !selectedNode" class="empty-state">
          <el-empty description="点击车辆或配件查看详情，右键点击添加新配件">
            <el-button type="primary" @click="refreshTree">刷新数据</el-button>
          </el-empty>
        </div>
        
        <!-- 车辆详情 -->
        <div v-else-if="selectedNode && selectedNode.type === 'vehicle' && vehicleDetail && !showAddForm" class="node-info">
          <h3>车辆详情</h3>
          <el-skeleton :loading="detailLoading" animated>
            <template #template>
              <div style="padding: 10px">
                <el-skeleton-item variant="text" style="width: 100%; height: 20px; margin-bottom: 10px" />
                <el-skeleton-item variant="text" style="width: 90%; height: 20px; margin-bottom: 10px" />
                <el-skeleton-item variant="text" style="width: 80%; height: 20px; margin-bottom: 10px" />
              </div>
            </template>
            <template #default>
              <div class="info-card">
                <div class="info-item">
                  <span class="label">车辆名称:</span>
                  <span class="value">{{ vehicleDetail.vehicleName }}</span>
                </div>
                <div class="info-item">
                  <span class="label">车辆编号:</span>
                  <span class="value">{{ vehicleDetail.vehicleCode }}</span>
                </div>
                <div class="info-item">
                  <span class="label">GPS信息:</span>
                  <span class="value">{{ vehicleDetail.gpsInfo || '无' }}</span>
                </div>
                <div class="info-item">
                  <span class="label">出厂时间:</span>
                  <span class="value">{{ vehicleDetail.outboundTime }}</span>
                </div>
                <div class="actions">
                  <el-button type="primary" @click="handleAddPartToSelected">添加配件</el-button>
                </div>
              </div>
            </template>
          </el-skeleton>
        </div>
        
        <!-- 配件详情 -->
        <div v-else-if="selectedNode && selectedNode.type === 'part' && partDetail && !showAddForm" class="node-info">
          <div class="clearfix">
            <h3>配件详情</h3>
            <div class="button-group">
              <el-button type="primary" @click="handleAddPartToSelected">添加配件</el-button>
              <el-button type="primary" @click="handleEditPart">编辑配件</el-button>
              <el-button type="warning" @click="showReplaceDialog">更换配件</el-button>
              <el-button type="danger" @click="handleDisassociatePart">解绑配件</el-button>
              <el-button type="danger" @click="handleScrapPart">报废配件</el-button>
              <el-button type="success" @click="showAssociateDialog">关联配件</el-button>
              <el-button type="info" @click="showHistoryDialog">操作历史</el-button>
            </div>
          </div>
          <el-skeleton :loading="detailLoading" animated>
            <template #template>
              <div style="padding: 10px">
                <el-skeleton-item variant="text" style="width: 100%; height: 20px; margin-bottom: 10px" />
                <el-skeleton-item variant="text" style="width: 90%; height: 20px; margin-bottom: 10px" />
                <el-skeleton-item variant="text" style="width: 80%; height: 20px; margin-bottom: 10px" />
              </div>
            </template>
            <template #default>
              <div class="info-card">
                <div class="info-grid">
                  <div class="info-column">
                    <div class="info-item">
                      <span class="label">配件类型:</span>
                      <span class="value">{{ getPartTypeName(partDetail.value.typeId) }}</span>
                    </div>
                    <div class="info-item">
                      <span class="label">配件编号:</span>
                      <span class="value">{{ partDetail.value.partCode }}</span>
                    </div>
                    <div class="info-item">
                      <span class="label">资产编号:</span>
                      <span class="value">{{ partDetail.value.assetNo || '无' }}</span>
                    </div>
                    <div class="info-item">
                      <span class="label">序列号:</span>
                      <span class="value">{{ partDetail.value.serialNo || '无' }}</span>
                    </div>
                    <div class="info-item">
                      <span class="label">电子标签码:</span>
                      <span class="value">{{ partDetail.value.rfidCode || '无' }}</span>
                    </div>
                   
                    <div class="info-item">
                      <span class="label">状态:</span>
                      <span class="value">{{ getPartStatusText(partDetail.value.status) }}</span>
                    </div>
                  </div>
                  <div class="info-column">
                    <div class="info-item">
                      <span class="label">单位:</span>
                      <span class="value">{{ partDetail.value.unit || '无' }}</span>
                    </div>
                    <div class="info-item">
                      <span class="label">规格型号:</span>
                      <span class="value">{{ partDetail.value.specification || '无' }}</span>
                    </div>
                    <div class="info-item">
                      <span class="label">品牌:</span>
                      <span class="value">{{ partDetail.value.brand || '无' }}</span>
                    </div>
                    <div class="info-item">
                      <span class="label">领用日期:</span>
                      <span class="value">{{ formatDate(partDetail.value.productionDate) }}</span>
                    </div>
                    <div class="info-item">
                      <span class="label">质保日期:</span>
                      <span class="value">{{ partDetail.value.warrantyExpiryDate ? formatDate(partDetail.value.warrantyExpiryDate) : '无' }}</span>
                    </div>
                    <div class="info-item">
                      <span class="label">使用日期:</span>
                      <span class="value">{{ partDetail.value.activationDate ? formatDate(partDetail.value.activationDate) : '未激活' }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </template>
          </el-skeleton>
        </div>
        
        <!-- 添加配件表单 -->
        <el-form
          v-if="showAddForm"
          ref="formRef"
          :model="formData"
          :rules="rules"
          label-width="120px"
          class="part-form"
        >
          <h3>{{ isEditMode ? '编辑配件' : '添加配件' }}</h3>
          <p class="form-subtitle" v-if="!isEditMode">
            添加到: 
            <strong>{{ currentNode?.data?.label }}</strong>
            <span v-if="currentNode?.data?.type === 'part'">({{ currentNode?.data?.typeName }})</span>
          </p>
          
          <div class="form-grid">
            <div class="form-column">
                <el-form-item label="配件类型" prop="typeId">
                <el-select v-model="formData.typeId" placeholder="请选择配件类型">
                  <el-option
                    v-for="type in partTypes"
                    :key="type.id"
                    :label="type.typeName"
                    :value="type.id"
                  />
                </el-select>
              </el-form-item>             

              <el-form-item label="资产编号" prop="assetNo">
                <el-input v-model="formData.assetNo" placeholder="请输入资产编号" />
              </el-form-item>

              <el-form-item label="序列号" prop="serialNo">
                <el-input v-model="formData.serialNo" placeholder="请输入序列号" />
              </el-form-item>

              <el-form-item label="电子标签码" prop="rfidCode">
                <el-input v-model="formData.rfidCode" placeholder="请输入电子标签码" />
              </el-form-item>
              <el-form-item label="领用日期" prop="productionDate">
                <el-date-picker
                  v-model="formData.productionDate"
                  type="date"
                  placeholder="选择领用日期"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                />
              </el-form-item>

                            <el-form-item label="状态" prop="status">
                <el-select v-model="formData.status" placeholder="请选择状态">
                  <el-option label="正常" value="1" />
                  <el-option label="维修中" value="2" />
                  <el-option label="报废" value="3" />
                </el-select>
              </el-form-item>
              
            </div>

            <div class="form-column">
              <el-form-item label="配件编号" prop="partCode">
                <el-input v-model="formData.partCode" placeholder="请输入配件编号" />
              </el-form-item>
              

              <el-form-item label="规格型号" prop="specification">
                <el-input v-model="formData.specification" placeholder="请输入规格型号" />
              </el-form-item>

              <el-form-item label="单位" prop="unit">
                <el-input v-model="formData.unit" placeholder="请输入单位" />
              </el-form-item>

              <el-form-item label="品牌" prop="brand">
                <el-input v-model="formData.brand" placeholder="请输入品牌" />
              </el-form-item>



              <el-form-item label="质保日期" prop="warrantyExpiryDate">
                <el-date-picker
                  v-model="formData.warrantyExpiryDate"
                  type="date"
                  placeholder="选择质保日期"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                />
              </el-form-item>
              
            </div>

            <div class="form-footer">
              <el-button type="primary" @click="submitForm" :loading="submitting">确认</el-button>
              <el-button @click="resetForm">取消</el-button>
            </div>
          </div>
        </el-form>

        <!-- 更换配件对话框 -->
        <el-dialog v-model="showReplaceForm" title="更换配件" width="800px">
          <el-form
            ref="replaceFormRef"
            :model="replaceFormData"
            :rules="rules"
            label-width="120px"
          >
            <el-form-item label="更换方式" prop="replaceType">
              <el-radio-group v-model="replaceFormData.replaceType">
                <el-radio label="new">新增配件</el-radio>
                <el-radio label="existing">关联已有配件</el-radio>
              </el-radio-group>
            </el-form-item>

            <!-- 关联已有配件 -->
            <template v-if="replaceFormData.replaceType === 'existing'">
              <el-form-item label="选择配件" prop="existingPartId">
                <el-select 
                  v-model="replaceFormData.existingPartId" 
                  placeholder="请选择未绑定的配件"
                  filterable
                >
                  <el-option
                    v-for="part in unassignedParts"
                    :key="part.id"
                    :label="part.partCode + ' - ' + getPartTypeName(part.typeId)"
                    :value="part.id"
                    :disabled="part.typeId !== partDetail.typeId"
                  />
                </el-select>
              </el-form-item>
            </template>

            <!-- 新增配件表单 -->
            <template v-else>
              <div class="form-grid">
                <div class="form-column">
                  <el-form-item label="配件类型">
                    <el-input :value="getPartTypeName(partDetail.typeId)" disabled />
                  </el-form-item>

                  <el-form-item label="资产编号" prop="assetNo">
                    <el-input v-model="replaceFormData.assetNo" placeholder="请输入新配件资产编号" />
                  </el-form-item>

                  <el-form-item label="序列号" prop="serialNo">
                    <el-input v-model="replaceFormData.serialNo" placeholder="请输入新配件序列号" />
                  </el-form-item>

                  <el-form-item label="电子标签码" prop="rfidCode">
                    <el-input v-model="replaceFormData.rfidCode" placeholder="请输入新配件电子标签码" />
                  </el-form-item>

                  <el-form-item label="领用日期" prop="productionDate">
                    <el-date-picker
                      v-model="replaceFormData.productionDate"
                      type="date"
                      placeholder="选择新配件领用日期"
                      format="YYYY-MM-DD"
                      value-format="YYYY-MM-DD"
                    />
                  </el-form-item>

                  <el-form-item label="状态" prop="status">
                    <el-select v-model="replaceFormData.status" placeholder="请选择新配件状态">
                      <el-option label="正常" value="1" />
                      <el-option label="维修中" value="2" />
                      <el-option label="报废" value="3" />
                    </el-select>
                  </el-form-item>
                </div>

                <div class="form-column">
                  <el-form-item label="配件编号" prop="partCode">
                    <el-input v-model="replaceFormData.partCode" placeholder="请输入新配件编号" />
                  </el-form-item>

                  <el-form-item label="规格型号" prop="specification">
                    <el-input v-model="replaceFormData.specification" placeholder="请输入新配件规格型号" />
                  </el-form-item>

                  <el-form-item label="单位" prop="unit">
                    <el-input v-model="replaceFormData.unit" placeholder="请输入新配件单位" />
                  </el-form-item>

                  <el-form-item label="品牌" prop="brand">
                    <el-input v-model="replaceFormData.brand" placeholder="请输入新配件品牌" />
                  </el-form-item>

                  <el-form-item label="质保日期" prop="warrantyExpiryDate">
                    <el-date-picker
                      v-model="replaceFormData.warrantyExpiryDate"
                      type="date"
                      placeholder="选择新配件质保日期"
                      format="YYYY-MM-DD"
                      value-format="YYYY-MM-DD"
                    />
                  </el-form-item>
                </div>
              </div>
            </template>
          </el-form>

          <template #footer>
            <el-button @click="showReplaceForm = false">取消</el-button>
            <el-button type="primary" @click="submitReplace" :loading="submitting">确认更换</el-button>
          </template>
        </el-dialog>

        <!-- 关联配件对话框 -->
        <el-dialog v-model="showAssociateForm" title="关联配件" width="500px">
          <!-- 提示信息 -->
          <el-alert
            title="为当前选择的配件关联子配件"
            type="info"
            :closable="false"
            show-icon
            class="mb-20"
          />
          <el-form label-width="120px">
            <!-- <el-form-item label="选择车辆" required>
              <el-select v-model="associateFormData.vehicleId" placeholder="请选择车辆">
                <el-option
                  v-for="vehicle in vehicles"
                  :key="vehicle.id"
                  :label="vehicle.vehicleName"
                  :value="vehicle.id"
                />
              </el-select>
            </el-form-item> -->
            
            <el-form-item label="选择子配件">
              <el-select 
                v-model="associateFormData.partId" 
                placeholder="请选择子配件"
                filterable
                clearable
              >
                <el-option
                  v-for="part in vehicleParts"
                  :key="part.id"
                  :label="`${part.partCode} (${getPartTypeName(part.typeId)})`"
                  :value="part.id"
                />
              </el-select>
            </el-form-item>
          </el-form>

          <template #footer>
            <el-button @click="showAssociateForm = false">取消</el-button>
            <el-button type="primary" @click="submitAssociate">确认关联</el-button>
          </template>
        </el-dialog>

        <!-- 操作历史对话框 -->
        <el-dialog v-model="showHistorForm" title="配件操作历史" width="800px">
          <el-table :data="operationHistory" v-loading="historyLoading" style="width: 100%">
            <el-table-column prop="operationType" label="操作类型" width="120">
              <template #default="{row}">
                <el-tag :type="getHistoryTagType(row.operationType)">
                  {{ getHistoryTypeText(row.operationType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="operationTime" label="操作时间" width="180">
              <template #default="{row}">
                {{ formatDateTime(row.operationTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="operator" label="操作人" width="120" />
            <el-table-column prop="remark" label="备注" />
            <el-table-column label="相关ID" width="100">
              <template #default="{row}">
                {{ row.relatedId || '-' }}
              </template>
            </el-table-column>
          </el-table>
        </el-dialog>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getParts, createPart, getPartByCode, updatePart, replacePart, disassociatePart, scrapPart, associatePart, getPartHistory } from '@/api/part'
import { getPartTypes } from '@/api/partType'
import { getVehicles, getVehicleById } from '@/api/vehicle'
import { Van, SetUp, Refresh } from '@element-plus/icons-vue'
import axios from '@/utils/request'
// 加载状态
const loading = ref(false)
const submitting = ref(false)
const detailLoading = ref(false)

// 操作历史相关
const showHistorForm = ref(false)
const operationHistory = ref([])
const historyLoading = ref(false)

// 关联配件相关
const showAssociateForm = ref(false)
const vehicles = ref([])
const vehicleParts = ref([])
const associateFormData = reactive({
  vehicleId: '',
  partId: null
})


// 树形数据
const treeData = ref([])
const treeRef = ref(null)
const defaultProps = {
  children: 'children',
  label: 'label'
}

// 右键菜单相关
const contextMenuVisible = ref(false)
const contextMenuStyle = ref({
  left: '0px',
  top: '0px'
})
const currentNode = ref(null)

// 节点选择相关
const selectedNode = ref(null)
const vehicleDetail = ref(null)
const partDetail = reactive({
  id: '',
  typeId: '',
  partCode: '',
  parentId: null,
  assetNo: '',
  serialNo: '',
  rfidCode: '',
  unit: '',
  specification: '',
  brand: '',
  productionDate: '',
  warrantyExpiryDate: '',
  status: '',
  activationDate: null,
  vehicleId: null
})

// 表单相关
const showAddForm = ref(false)
const isEditMode = ref(false)
const formRef = ref(null)

// 更换配件相关
const showReplaceForm = ref(false)
const replaceFormRef = ref(null)
const unassignedParts = ref([])

// 获取根节点车辆ID
const getRootVehicleId = (node) => {
  if (!node) return null
  if (node.type === 'vehicle') return node.vehicleId
  
  // 在树数据中查找当前节点的根节点
  const findRootVehicle = (nodes, targetId) => {
    for (const node of nodes) {
      if (node.type === 'vehicle' && hasNodeInChildren(node, targetId)) {
        return node.vehicleId
      }
    }
    return null
  }
  
  return findRootVehicle(treeData.value, node.id)
}

// 检查节点是否在子节点中
const hasNodeInChildren = (node, targetId) => {
  if (node.id === targetId) return true
  if (node.children) {
    return node.children.some(child => hasNodeInChildren(child, targetId))
  }
  return false
}
const replaceFormData = reactive({
  replaceType: 'new',
  existingPartId: '',
  partCode: '',
  assetNo: '',
  serialNo: '',
  rfidCode: '',
  typeId: '',
  unit: '',
  specification: '',
  brand: '',
  productionDate: '',
  warrantyExpiryDate: '',
  status: '1'
})

// 加载未分配的配件(与当前配件类型相同且未关联车辆)
const loadUnassignedParts = async () => {
  try {
    const response = await getParts({
      pageNum: 1,
      pageSize: 9999,
      unassigned: true,
      typeId: partDetail.typeId // 只获取相同类型的配件
    })
    // 过滤掉已关联到车辆的配件
    unassignedParts.value = (response.records || []).filter(part => !part.vehicleId)
  } catch (error) {
    console.error('加载未分配配件失败:', error)
    ElMessage.error('加载未分配配件失败')
  }
}
const formData = reactive({
  id: null,
  partCode: '',
  assetNo: '',
  serialNo: '',
  rfidCode: '',
  typeId: '',
  unit: '',
  specification: '',
  brand: '',
  productionDate: '',
  warrantyExpiryDate: '',
  activationDate: null,
  status: '1',
  vehicleId: null,
  parentId: null
})

// 处理编辑配件
const handleEditPart = () => {
  isEditMode.value = true
  showAddForm.value = true
  
  // 复制当前配件数据到表单
  Object.assign(formData, {
    id: partDetail.value.id,
    partCode: partDetail.value.partCode,
    assetNo: partDetail.value.assetNo || '',
    serialNo: partDetail.value.serialNo || '',
    rfidCode: partDetail.value.rfidCode || '',
    typeId: partDetail.value.typeId,
    unit: partDetail.value.unit || '',
    specification: partDetail.value.specification || '',
    brand: partDetail.value.brand || '',
    productionDate: partDetail.value.productionDate,
    warrantyExpiryDate: partDetail.value.warrantyExpiryDate || '',
    status: partDetail.value.status || '1',
    activationDate: partDetail.value.activationDate || null,
    vehicleId: partDetail.value.vehicleId,
    parentId: partDetail.value.parentId
  })
}

// 显示操作历史对话框
const showHistoryDialog = async () => {
  if (!partDetail.value?.id) {
    ElMessage.warning('请先选择配件')
    return
  }
  
  showHistorForm.value = true
  historyLoading.value = true
  
  try {
    const response = await getPartHistory(partDetail.value.id)
    operationHistory.value = response || []
  } catch (error) {
    console.error('获取操作历史失败:', error)
    ElMessage.error('获取操作历史失败')
  } finally {
    historyLoading.value = false
  }
}

// 显示关联配件对话框
const showAssociateDialog = async () => {
  showAssociateForm.value = true
  try {
   const response = await getParts({
        pageNum: 1,
        pageSize: 9999,
        bindStatus: 0, // 只获取未绑定车辆的配件
    })
      vehicleParts.value = response.records || []
  } catch (error) {
    console.error('加载配件列表失败:', error)
    ElMessage.error('加载车辆列表失败')
  }
}
 
// 提交关联配件
const submitAssociate = async () => {
  // if (!associateFormData.vehicleId) {
  //   ElMessage.warning('请选择车辆')
  //   return
  // }

  try {
    submitting.value = true


    // 直接使用 axios 发送请求（绕过拦截器）
    axios.put(`/parts/${associateFormData.partId}/associate-vehicle`, null, {
      params: { vehicleId: partDetail.value.vehicleId, parentId: partDetail.value.id }
    }).then(response => {
      console.log("请求成功:", response);
    }).catch(error => {
      console.error("请求失败:", error.config); // 查看实际的请求配置
    });

    // await associatePart({
    //   id: partDetail.value.id,
    //   vehicleId: associateFormData.vehicleId,
    //   parentId: associateFormData.parentId || null
    // })
    
    ElMessage.success('关联配件成功')
    showAssociateForm.value = false
    await refreshTree()
    
    // 重置表单数据
    associateFormData.vehicleId = ''
    associateFormData.partId = null
  } catch (error) {
    console.error('关联配件失败:', error)
    ElMessage.error(error.response?.data?.message || '关联配件失败')
  } finally {
    submitting.value = false
  }
}

// 处理解绑配件
const handleDisassociatePart = async () => {
  try {
    // 判断当前配件的节点是否有下级配件，如果有则不允许解绑
    if (partDetail.value.children && partDetail.value.children.length > 0) {
      ElMessage.warning('当前配件存在下级配件，不允许解绑')
      return
    }

    await ElMessageBox.confirm('确定要解绑此配件吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    submitting.value = true
    await disassociatePart(partDetail.value.id)
    
    ElMessage.success('解绑配件成功')
    await refreshTree()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('解绑配件失败:', error)
      ElMessage.error(error.response?.data?.message || '解绑配件失败')
    }
  } finally {
    submitting.value = false
  }
}

// 处理报废配件
const handleScrapPart = async () => {
  try {
    const { value: remark } = await ElMessageBox.prompt('请输入报废原因', '报废配件', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
      inputPlaceholder: '请输入报废原因（选填）'
    })
    
    submitting.value = true
    await scrapPart(partDetail.value.id, remark)
    
    ElMessage.success('报废配件成功')
    await refreshTree()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('报废配件失败:', error)
      ElMessage.error(error.response?.data?.message || '报废配件失败')
    }
  } finally {
    submitting.value = false
  }
}

// 显示更换配件对话框
// 监听更换方式变化
watch(() => replaceFormData.replaceType, (newType) => {
  if (newType === 'existing') {
    // 切换到关联已有配件时，清空新配件相关字段
    Object.assign(replaceFormData, {
      partCode: '',
      assetNo: '',
      serialNo: '',
      rfidCode: '',
      unit: '',
      specification: '',
      brand: '',
      productionDate: '',
      warrantyExpiryDate: '',
      status: '1'
    })
  } else {
    // 切换到新增配件时，清空已有配件ID
    replaceFormData.existingPartId = ''
  }
})

// 显示更换配件对话框
const showReplaceDialog = async () => {
  // 检查当前配件是否有下级配件
  if (selectedNode.value.children && selectedNode.value.children.length > 0) {
    ElMessage.warning('当前配件存在下级配件，请先移除下级配件后再更换')
    return
  }

  // 保存当前配件信息
  Object.assign(partDetail, {
    id: selectedNode.value.partId,
    typeId: selectedNode.value.typeId,
    partCode: selectedNode.value.label,
    parentId: selectedNode.value.parentId
  })

  // 重置表单数据
  Object.assign(replaceFormData, {
    replaceType: 'new',
    existingPartId: '',
    partCode: '',
    assetNo: '',
    serialNo: '',
    rfidCode: '',
    typeId: partDetail.typeId, // 使用当前配件的类型
    unit: '',
    specification: '',
    brand: '',
    productionDate: '',
    warrantyExpiryDate: '',
    status: '1'
  })

  // 加载未分配的配件
  try {
    await loadUnassignedParts()
  } catch (error) {
    console.error('加载未分配配件失败:', error)
    ElMessage.error('加载未分配配件失败')
  }

  showReplaceForm.value = true
}

// 提交更换配件
const submitReplace = async () => {
  if (!replaceFormRef.value) return
  
  try {
    await replaceFormRef.value.validate()
    submitting.value = true
    
    // 获取根节点车辆ID
    const rootVehicleId = getRootVehicleId(selectedNode.value)
    if (!rootVehicleId) {
      ElMessage.error('无法确定所属车辆')
      return
    }

    let response
    if (replaceFormData.replaceType === 'existing') {
      // 关联已有配件
      if (!replaceFormData.existingPartId) {
        ElMessage.warning('请选择要关联的配件')
        return
      }
      
      // 获取旧配件的parentId
      const parentId = partDetail.parentId || null
      console.log('parentId:', parentId)
      
      response = await replacePart({
        oldPartId: partDetail.id,
        newPartId: replaceFormData.existingPartId,
        vehicleId: rootVehicleId,
        parentId: parentId  // 继承旧配件的parentId
      })
    } else {
      // 新增配件
      // 获取旧配件的parentId
      const parentId = partDetail.parentId || null
      console.log('parentId:', parentId)
      
      const newPart = {
        ...replaceFormData,
        typeId: partDetail.typeId, // 使用原配件的类型
        vehicleId: rootVehicleId,
        parentId: parentId  // 继承旧配件的parentId
      }
      console.log('新配件数据:', newPart)
      
      response = await replacePart({
        oldPartId: partDetail.id,
        newPart: newPart
      })
    }
    
    ElMessage.success('更换配件成功')
    showReplaceForm.value = false
    await refreshTree()
  } catch (error) {
    console.error('更换配件失败:', error)
    ElMessage.error(error.response?.data?.message || '更换配件失败')
  } finally {
    submitting.value = false
  }
}

// 配件类型列表
const partTypes = ref([])

// 表单验证规则
const rules = {
  replaceType: [{ required: true, message: '请选择更换方式', trigger: 'change' }],
  existingPartId: [{ 
    required: true, 
    message: '请选择要关联的配件', 
    trigger: 'change',
    validator: (rule, value, callback) => {
      if (replaceFormData.replaceType === 'existing' && !value) {
        callback(new Error('请选择要关联的配件'))
      } else {
        callback()
      }
    }
  }],
  typeId: [{ required: true, message: '请选择配件类型', trigger: 'change' }],
  partCode: [{ required: true, message: '请输入配件编号', trigger: 'blur' }],
  assetNo: [{ required: false, message: '请输入资产编号', trigger: 'blur' }],
  serialNo: [{ required: false, message: '请输入序列号', trigger: 'blur' }],
  rfidCode: [{ required: false, message: '请输入电子标签码', trigger: 'blur' }],
  unit: [{ required: false, message: '请输入单位', trigger: 'blur' }],
  specification: [{ required: false, message: '请输入规格型号', trigger: 'blur' }],
  brand: [{ required: false, message: '请输入品牌', trigger: 'blur' }],
  productionDate: [{ required: false, message: '请选择领用日期', trigger: 'change' }],
  warrantyExpiryDate: [{ required: false, message: '请选择质保日期', trigger: 'change' }],
  status: [{ required: false, message: '请选择状态', trigger: 'change' }]
}

// 处理节点点击
const handleNodeClick = async (data, node) => {
  selectedNode.value = data
  showAddForm.value = false
  
  // 清除之前的详情数据
  vehicleDetail.value = null
  partDetail.value = null
  
  detailLoading.value = true
  try {
    if (data.type === 'vehicle') {
      // 获取车辆详情
      const response = await getVehicleById(data.vehicleId)
      vehicleDetail.value = response
    //   if (response.data.code === 200) {
    //     vehicleDetail.value = response.data.data
    //   } else {
    //     ElMessage.error('获取车辆详情失败')
    //   }
    } else if (data.type === 'part') {
      // 获取配件详情
      const response = await getPartByCode(data.label)
      partDetail.value = response

    //   if (response.data.code === 200) {
    //     partDetail.value = response.data.data
    //   } else {
    //     ElMessage.error('获取配件详情失败')
    //   }
    }
  } catch (error) {
    ElMessage.error('获取详情数据失败')
    console.error(error)
  } finally {
    detailLoading.value = false
  }
}

// 获取配件类型名称
const getPartTypeName = (typeId) => {
  const type = partTypes.value.find(t => t.id === typeId)
  return type ? type.typeName : '未知类型'
}

// 获取操作历史类型文本
const getHistoryTypeText = (type) => {
  const typeMap = {
    1: '领用',
    4: '维修',
    5: '报废',
    'REPLACE': '更换',
    2: '关联',
    3: '解绑'
  }
  return typeMap[type] || type
}

// 获取操作历史标签样式
const getHistoryTagType = (type) => {
  const typeMap = {
    1: 'success',
    4: 'warning',
   5: 'danger',
    'REPLACE': 'primary', 
    2: 'info',
    3: 'danger'
  }
  return typeMap[type] || ''
}

// 格式化日期时间
const formatDateTime = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

// 获取配件状态文本
const getPartStatusText = (status) => {
  const statusMap = {
    0: '禁用',
    1: '正常',
    2: '维修中',
    3: '报废'
  }
  return statusMap[status] || '未知状态'
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

// 从选中节点添加配件
const handleAddPartToSelected = () => {
  if (!selectedNode.value) return
  currentNode.value = { data: selectedNode.value }
  handleAddPart()
}

// 刷新树形数据
const refreshTree = async () => {
  loading.value = true
  try {
    await initData()
    // ElMessage.success('数据刷新成功')
  } catch (error) {
    ElMessage.error('数据刷新失败')
  } finally {
    loading.value = false
  }
}

// 初始化数据
const initData = async () => {
  loading.value = true
  try {
    // 获取车辆列表
    const vehicleRes = await getVehicles()
    console.log('车辆数据:', vehicleRes)
    const vehicles = vehicleRes.records

    // 获取配件类型列表
    const typeRes = await getPartTypes({
      
    })
    console.log('配件类型数据:', typeRes)
    partTypes.value = typeRes.records

    // 获取配件列表
    const partsRes = await getParts({
      pageNum: 1,
      pageSize: 9999
    })
    
    const parts = partsRes.records

    // 构建树形数据
    treeData.value = vehicles.map(vehicle => ({
      id: `vehicle_${vehicle.id}`,
      label: vehicle.vehicleName,
      vehicleId: vehicle.id,
      type: 'vehicle',
      children: buildPartTree(parts.filter(part => part.vehicleId === vehicle.id && !part.parentId), parts)
    }))
    
    // 重置选中状态
    selectedNode.value = null
    showAddForm.value = false
  } catch (error) {
    ElMessage.error('加载数据失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 递归构建配件树
const buildPartTree = (rootParts, allParts) => {
  return rootParts.map(part => {
    const node = convertPartToNode(part)
    const children = allParts.filter(p => p.parentId === part.id)
    if (children.length > 0) {
      node.children = buildPartTree(children, allParts)
    }
    return node
  })
}

// 将配件数据转换为树节点
const convertPartToNode = (part) => {
  const partType = partTypes.value.find(type => type.id === part.typeId)
  return {
    id: `part_${part.id}`,
    label: part.partCode,
    typeName: partType?.typeName,
    partId: part.id,
    typeId: part.typeId,
    vehicleId: part.vehicleId,
    type: 'part',
    children: []
  }
}

// 处理节点右键点击
const handleNodeRightClick = (event, data, node, element) => {
  event.preventDefault()
  currentNode.value = { data, node }
  selectedNode.value = data
  
  // 显示右键菜单
  contextMenuVisible.value = true
  contextMenuStyle.value = {
    left: event.clientX + 'px',
    top: event.clientY + 'px'
  }
}

// 隐藏右键菜单
const hideContextMenu = () => {
  contextMenuVisible.value = false
}

// 处理添加配件
const handleAddPart = () => {
  if (!currentNode.value) return

  const { data } = currentNode.value
  
  // 获取根节点车辆ID
  const rootVehicleId = getRootVehicleId(data)
  if (!rootVehicleId) {
    ElMessage.error('无法确定所属车辆')
    return
  }
  
  // 设置vehicleId为根节点车辆ID，parentId根据当前节点类型设置
  formData.vehicleId = rootVehicleId
  formData.parentId = data.type === 'part' ? data.partId : null

  showAddForm.value = true
  hideContextMenu()
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    submitting.value = true
    
    // 设置使用日期为当前日期
    formData.activationDate = new Date().toISOString().split('T')[0]
    
    let response
    if (isEditMode.value) {
      // 更新配件
      response = await updatePart(formData)
      console.log('更新配件响应:', response)
      
      // 更新树节点
      updateTreeNode(response)
      
      // 更新详情数据
      if (selectedNode.value && selectedNode.value.partId === formData.id) {
        partDetail.value = response
      }
      
      ElMessage.success('更新配件成功')
    } else {
      // 创建配件
      response = await createPart(formData)
      console.log('创建配件响应:', response)
      
      // 更新树形结构
      updateTreeWithNewPart(response)
      
      ElMessage.success('添加配件成功')
    }
    
    // 重置表单和状态
    showAddForm.value = false
    isEditMode.value = false
    resetForm()
    
  } catch (error) {
    ElMessage.error(isEditMode.value ? '更新配件失败' : '添加配件失败')
    console.error(error)
  } finally {
    submitting.value = false
  }
}

// 更新树节点
const updateTreeNode = (updatedPart) => {
  const updateNode = (nodes) => {
    for (const node of nodes) {
      if (node.type === 'part' && node.partId === updatedPart.id) {
        // 更新节点数据
        node.label = updatedPart.partCode
        const partType = partTypes.value.find(type => type.id === updatedPart.typeId)
        node.typeName = partType?.typeName
        node.typeId = updatedPart.typeId
        return true
      }
      if (node.children && node.children.length > 0) {
        if (updateNode(node.children)) {
          return true
        }
      }
    }
    return false
  }
  
  updateNode(treeData.value)
}

// 更新树形结构，添加新配件
const updateTreeWithNewPart = async (newPart) => {
  // 获取配件类型信息
  const partType = partTypes.value.find(type => type.id === newPart.typeId)
  
  // 创建新的树节点
  const newNode = {
    id: `part_${newPart.id}`,
    label: newPart.partCode,
    typeName: partType?.typeName,
    partId: newPart.id,
    typeId: newPart.typeId,
    vehicleId: newPart.vehicleId,
    type: 'part',
    children: []
  }
  
  // 找到父节点并添加新节点
  if (newPart.parentId) {
    // 如果有父配件，找到父配件节点
    const findAndAddToParent = (nodes) => {
      for (const node of nodes) {
        if (node.type === 'part' && node.partId === newPart.parentId) {
          node.children.push(newNode)
          return true
        }
        if (node.children && node.children.length > 0) {
          if (findAndAddToParent(node.children)) {
            return true
          }
        }
      }
      return false
    }
    
    findAndAddToParent(treeData.value)
  } else {
    // 如果直接挂在车辆下，找到对应的车辆节点
    const vehicleNode = treeData.value.find(node => 
      node.type === 'vehicle' && node.vehicleId === newPart.vehicleId
    )
    
    if (vehicleNode) {
      vehicleNode.children.push(newNode)
    }
  }
  
  // 更新选中节点
  selectedNode.value = newNode
  
  // 如果有树引用，展开父节点
  if (treeRef.value) {
    if (newPart.parentId) {
      const parentKey = `part_${newPart.parentId}`
      treeRef.value.store.nodesMap[parentKey].expanded = true
    } else {
      const vehicleKey = `vehicle_${newPart.vehicleId}`
      treeRef.value.store.nodesMap[vehicleKey].expanded = true
    }
  }
}

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  showAddForm.value = false
}

// 页面加载时初始化数据
onMounted(() => {
  initData()
  
  // 添加点击外部关闭右键菜单
  document.addEventListener('click', (event) => {
    if (contextMenuVisible.value) {
      hideContextMenu()
    }
  })
})
</script>

<style scoped>
.vehicle-parts-config {
  height: 100%;
  padding: 20px;
}

.tree-panel {
  border-right: 1px solid #dcdfe6;
  padding: 20px;
  height: calc(100vh - 120px);
  overflow: auto;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.tree-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e4e7ed;
}

.tree-header h3 {
  margin: 0;
  color: #303133;
}

.form-panel {
  padding: 20px;
  height: calc(100vh - 120px);
  overflow: auto;
}

.part-form {
  max-width: 900px;
  background-color: #fff;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.part-form :deep(.el-form-item) {
  margin-bottom: 18px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 32px;
  margin: 0 auto;
}

.form-column {
  min-width: 0;
}

.form-column :deep(.el-form-item__content) {
  width: 100%;
}

.form-column :deep(.el-input),
.form-column :deep(.el-select),
.form-column :deep(.el-date-picker) {
  width: 100%;
}

@media (max-width: 768px) {
  .form-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .form-footer {
    grid-column: 1;
  }
}

.form-footer {
  grid-column: span 2;
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px dashed #e4e7ed;
}

.form-subtitle {
  color: #606266;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px dashed #e4e7ed;
}

.custom-tree-node {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 2px 0;
}

.part-type {
  color: #909399;
  font-size: 12px;
}

.context-menu {
  position: fixed;
  background: #fff;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  z-index: 9999;
}

.context-menu ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.context-menu li {
  padding: 8px 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.context-menu li:hover {
  background-color: #ecf5ff;
  color: #409eff;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}

.node-info {
  padding: 20px;
}

.info-card {
  background-color: #fff;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.info-column {
  min-width: 0;
}

.clearfix {
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e4e7ed;
}

.clearfix h3 {
  margin: 0;
  padding-bottom: 10px;
  border-bottom: none;
}

.button-group {
  display: flex;
  gap: 10px;
  margin-top: 10px;
}

.info-card:hover {
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.1);
}

.info-item {
  margin-bottom: 15px;
  display: flex;
  align-items: flex-start;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-item:last-child {
  border-bottom: none;
}

.info-item .label {
  font-weight: bold;
  width: 100px;
  color: #606266;
  flex-shrink: 0;
}

.info-item .value {
  color: #303133;
  flex-grow: 1;
  word-break: break-all;
}

.actions {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px dashed #e4e7ed;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.node-info h3 {
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e4e7ed;
  color: #303133;
  font-size: 18px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.node-info h3::before {
  content: '';
  width: 4px;
  height: 16px;
  background-color: #409eff;
  border-radius: 2px;
  display: inline-block;
}

:deep(.el-tree-node.is-current > .el-tree-node__content) {
  background-color: #ecf5ff;
}

:deep(.el-tree-node__content:hover) {
  background-color: #f5f7fa;
}
</style>